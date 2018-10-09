package com.loan

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.json.JsonOutput
import org.apache.commons.codec.digest.DigestUtils
import sun.security.provider.MD5
import com.loan.*

import java.text.SimpleDateFormat

class PaymentController
{

    def md5Service
    def signatureUtilService

    private final String ENCODEING = "UTF-8"
    private final String merchantNumber = "0001000F0397137"
    private final String password = "xf1k0hsvgm9yvtd2hnllajqe4rcmnpgd"
    private final String key = "jvmda9d2sg2tgfjlagojwaty9vlfy32t"
    private final String version = "2.0"
    private final String username = "DSF397137fhy"

    private final String merchantName = "中佳信"
    private final String idType = "0"
    private final String peojectDeadline = "3650"



    //分

    //跳转到签约页面
    def index()
    {

    }

    //发送HTTP请求
    def requestPost(url, params)
    {
        URL urlString = new URL(url)
        println url
        HttpURLConnection urlConnection = (HttpURLConnection) urlString.openConnection()
        urlConnection.setRequestMethod('POST')
        urlConnection.setDoOutput(true)
        urlConnection.setReadTimeout(10000)
        urlConnection.outputStream.withWriter(ENCODEING) { Writer writer -> writer.write params }
        def responeResult = urlConnection.inputStream.withReader(ENCODEING) { Reader reader -> reader.text }
        return responeResult
    }

    //AJAX五要素生成签名
    def fiveElementSign()
    {
        def userAccountInformation = new UserAccountInformation()
        String srcChnl = params.srcChnl
        //签约来源
        String busiCd = params.busiCd
        //业务类型 都是默认的AC01
        String bankCd = params.bankCd
        //行别
        String userNm = params.userNm
        //姓名
        String mobileNo = params.mobileNo;
        //手机号
        String credtTp = params.credtTp
        //证件类型
        String credtNo = params.credtNo
        //证件号码
        String acntTp = params.acntTp
        //账户类型 只支持借记卡
        String acntNo = params.acntNo
        //银行卡号
        String mchntCd = params.mchntCd
        //商户号
        String isCallback = params.isCallback
        //默认值:0，回拨签约功能暂未开通
        String reserved1 = params.reserved1
        //保留字段，长度介于 1 到 60 位之间字符串，支持中文
        String pageFrontUrl = "http://10.0.8.165/payment/project"
        //前台地址
        def list = []
        list.add(isCallback);
        list.add(busiCd);
        list.add(credtTp);
        list.add(acntNo);
        list.add(bankCd);
        list.add(userNm);
        list.add(credtNo);
        list.add(srcChnl);
        list.add(acntTp);
        list.add(mobileNo);
        list.add(mchntCd);
        list.add(reserved1);
        list.add(pageFrontUrl)
        String s = signatureUtilService.hex(list, key);
        System.out.println(s);
        userAccountInformation.save flush: true
        render(["signature": s] as JSON)
    }

    @Transactional
    //5要素签约异步回调
    def signCallBack()
    {

        /* 第一步:组装 signature 加密原始字符串
         （mercht_pfx：商户秘钥） String signSource=mchnt_cd +"|"+ contract_no+"|"+acnt_no+"|"
         + (yyyyMMdd 格式日 期)+"|"+ contract_st+"|"+ mercht_pfx;
         第二步:使用 sha1 算法进行加密 String signature=DigestUtils. shaHex (signSource);*/
        //得到加密签名
        def signature = params.signature
        def contract_no = params.contract_no
        //订单号
        def acnt_no = params.acnt_no
        //银行卡号
        def contract_st = params.contract_st
        //订单状态
        def mercht_pfx = "123456"
        //商户密码
        String signSource = merchantNumber + "|" + contract_no + "|" + acnt_no + "|" + new Date().format("yyyyMMdd") + "|" + contract_st + "|" + mercht_pfx
        String signatureCheck = DigestUtils.shaHex(signSource)
        /*1 已生效（注：签约成功） 0 未生效 （注：签约失败） 2 待验证 （注：等待确认）*/
        if (signature == signatureCheck)
        {
            def contract_pay_code = request.getParameter("contract_pay_code")
            //交易状态码
            def contract_pay_error = request.getParameter("contract_pay_error")
            //交易说明
            /*
             def contract_pay_code = request.getParameter("contract_pay_code")  //交易状态码
             def contract_pay_error = request.getParameter("contract_pay_error") //交易说明
             def userAccount = UserAccountInformation.findByNumberOfAccount(acnt_no)
             sign.serialNumber = contract_no
             sign.status = contract_st
             sign.statusCode = contract_pay_code
             sign.description = contract_pay_error*/
        }
        render "1"
    }

    //签约查询   三要素：商户号 ，操作员代码  ， 银行卡号
    /*
    * 根据银行卡号查询到用户的信息，保存以后代收代付使用*/

    def signQuery()
    {

        def data = request.JSON
        println data
        def numberOfAccount = data["numberOfAccount"]
        def amt = data["amount"]
        int amount =new  BigDecimal(amt * 10000 * 100 ).setScale(0,BigDecimal.ROUND_HALF_UP).toInteger()

        def bankName = data["bankName"]
        println numberOfAccount
        def userAccount = UserAccountInformation.findByNumberOfAccount(numberOfAccount)

        try
        {
            String mchntCd = merchantNumber
            //商户号
            String recUpdUsr = username
            //操作员代码
            String acntNo = numberOfAccount
            //账号 必填

            ArrayList<String> list = new ArrayList<String>()
            list.add(mchntCd)
            list.add(recUpdUsr)
            list.add(acntNo)

            String signature = signatureUtilService.hex(list, password)
            //得到验签
            //得到xml
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><custmrBusi><mchntCd>" + mchntCd + "</mchntCd><recUpdUsr>" + recUpdUsr + "</recUpdUsr><acntNo>" + acntNo + "</acntNo><signature>" + signature + "</signature></custmrBusi>";

            String loginUrl = "https://fht.fuiou.com/fuMer/api_query.do"
            def responseXml = requestPost(loginUrl, "xml=" + xml)
            def resultStr = URLDecoder.decode(responseXml, "GBK")
            def rootNode = new XmlParser().parseText(resultStr)
            println resultStr
            def isSuccess
            def fullName
            def idNumber
            def mobileNo
            def respCd
            def respDesc

            respCd = rootNode.respCd.text() //响应码
            respDesc = rootNode.respDesc.text() //响应描述

            rootNode.custmrBusi.each {
                isSuccess = it.contractSt.text() //是否生效
                fullName = it.userNm.text() // 户名
                idNumber = it.credtNo.text() //证件号
                acntNo = it.acntNo.text() //卡号
                mobileNo = it.mobileNo.text() //手机号
            }
            if (respCd){
                def map = [:]
                map.put("code",100)
                map.put("desc",respDesc)
                def result = JsonOutput.toJson(map).toString()
                render   result
            } else {
                if(!userAccount){
                    userAccount = new UserAccountInformation()
                    userAccount.idNumber = idNumber
                    userAccount.cellphone = mobileNo
                    userAccount.fullName = fullName
                    userAccount.numberOfAccount = acntNo
                    userAccount.responseCode = respCd
                    userAccount.responseDesc = respDesc
                    if(userAccount.validate()){
                        userAccount.save flush:true
                    } else {
                        println  userAccount.errors
                    }
                }
                render   projectEntry(idNumber,numberOfAccount,fullName,mobileNo,amount,bankName)

            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /*def signQuery(){
        def data = request.JSON
        println data
        def numberOfAccount = data["numberOfAccount"]
        int amount = data["amount"] * 10000 * 100
        def bankName = data["bankName"]
        def idNumber = data["idNumber"]
        def fullName = data["fullName"]
        def mobileNo = data["cellPhone"]
        def userAccount = new UserAccountInformation()
        userAccount.idNumber = idNumber
        userAccount.cellphone = mobileNo
        userAccount.fullName = fullName
        userAccount.numberOfAccount = numberOfAccount
                def result = projectEntry(idNumber,numberOfAccount,fullName,mobileNo,amount,bankName)
                render  result





    }*/
    def projectEntry (idNumber,numberOfAccount,fullName,mobileNo,amount,bankName)
    {
        def id_no = idNumber
        //借款人证件号码
        def project = new ProjectEntryInformation()
        def ver = version
        //版本

        def orderno = System.currentTimeMillis()
        project.serialNumber = orderno
        //请求流水

        def merchantName = merchantName
        //商户名称

        def project_ssn = new Date().format("HHmmss")
        project.projectNumber = project_ssn
        //项目序列号
        def project_amt = amount
        project.amount = amount
        //项目金额
        def contract_nm = "zix" + System.currentTimeMillis()
        project.contractNumber = contract_nm
        //商户借款合同号
        def project_deadline = peojectDeadline
        project.deadline = project_deadline
        //项目期限  （天）

        def bor_nm = fullName
        //价款人姓名
        def id_tp = "0"
        //借款人证件类型
        def card_no = numberOfAccount
        project.numberOfAccount = card_no
        //借款人卡号
        def mobile_no = mobileNo
        //借款人手机号
        try
        {
            String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?><project><ver>" + ver + "</ver><orderno>" + orderno + "</orderno><mchnt_nm>" + merchantName + "</mchnt_nm><project_ssn>" + project_ssn + "</project_ssn><project_nm></project_nm><project_usage></project_usage><project_amt>" + project_amt + "</project_amt><expected_return></expected_return><project_fee></project_fee><contract_nm>" + contract_nm + "</contract_nm><project_deadline>" + project_deadline + "</project_deadline><raise_deadline></raise_deadline><max_invest_amt></max_invest_amt><max_invest_num></max_invest_num><min_invest_num></min_invest_num><each_invest_am></each_invest_am><bor_nm>" + bor_nm + "</bor_nm><bor_sex>0</bor_sex><id_tp>" + id_tp + "</id_tp><id_no>" + id_no + "</id_no><card_no>" + card_no + "</card_no><mobile_no>" + mobile_no + "</mobile_no></project>";
            String macSource = merchantNumber + "|" + password + "|" + xml
            String mac = md5Service.encode(macSource, "UTF-8").toUpperCase()
            String loginUrl = "https://fht.fuiou.com/inspro.do"
            def params = "merid=" + merchantNumber + "&xml=" + xml + "&mac=" + mac
            println params

            def result = requestPost(loginUrl, params);
            def resultStr = URLDecoder.decode(result, "GBK")
            //000014927688517340345142_20170421_370612项目录入成功
            //<?xml version="1.0" encoding="UTF-8" standalone="yes"?><project><ret>0000</ret><orderno>1492768851734</orderno><project_id>0345142_20170421_370612</project_id><memo>项目录入成功</memo></project>
            def rootNode = new XmlParser().parseText(resultStr)
            def ret = rootNode.ret.text()
            //响应码
            def orderNo = rootNode.orderno.text()
            //商户请求流水
            def project_id = rootNode.project_id.text()
            // 项目ID
            def memo = rootNode.memo.text()
            // 描述
            println ret + memo
            project.responseCode = ret
            project.projectId = project_id
            project.responseDescribe = memo
            project.serialNumber = orderNo
            if(project.validate()){
                project.save flush:true
            } else {
                println project.errors
            }
            def map = [:]
            println project
            if (ret == "0000")
            {
                println "项目入路"
                makeCollections(idNumber,numberOfAccount,fullName,mobileNo,project_id,bankName,amount)
            } else {
                map.put("code",200)
                map.put("desc",memo)
                return JsonOutput.toJson(map).toString()
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //项目审核通知 （回调）
    def projectAuditNotification()
    {
        println "项目审核回调"
        def mchnt_cd = params.mchnt_cd
        //商户代码  富友分配给各合作商户的唯一识别码
        def mchnt_txn_ssn = params.mchnt_txn_ssn
        //流水号
        def project_id = params.project_id
        //项目 ID
        def project_dt = params.project_dt
        //录入时间
        def project_st = params.project_st
        //结果  1
        def remark = params.remark
        //备注
        def signature = params.signature
        def signatureSource = mchnt_cd + "|" + mchnt_txn_ssn + "|" + project_id + "|" + project_dt + "|" + project_st + "|" + remark
        signatureSource = md5Service.MD5Encode(signatureSource, "UTF-8")
        def projectReport = ProjectEntryInformation.findByProjectId(project_id)
        if (signature == signatureSource)
        {
            projectReport.status = project_st
            projectReport.remark = remark
            projectReport.save()
        }

        render "1"
    }

    //支付
    def main()
    {

    }

    //付款  代付
    def payForCollection()
    {
        def data = request.JSON
        def payment = new PaymentInfromation()
        try
        {
            int amount = data["amount"] * 10000 * 100
            def bankCode = Bank.findByName(data["bankName"]).code
            def cityCode = City.findByName(data["city"]).code
            def bankBranch = data["bankBranch"]
            def bankCardNumber = data["bankCardNumber"]
            def fullName = data["fullName"]
            def cellphone = data["cellphone"]
            def ver = version
            //版本号
            def merdt = new Date().format("yyyyMMdd")
            payment.requestDate = merdt
            //请求日期
            def orderno = new Date().getTime()
            payment.serialNumber = orderno
            //请求流水
            def amt = amount
            payment.amount = amt
            //金额
            def bankno = bankCode
            //总行代码
            def cityno = cityCode
            //城市代码
            def branchnm = bankBranch
            // 支行名称  （支行名称，中行、建行、广发必填）
            def accntno = bankCardNumber
            payment.numberOfAccount = bankCardNumber
            //账号
            def accntnm = fullName
            //账户名称
            def mobile = cellphone
            //手机号
            def reqtype = "payforreq"
            String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>" + "<payforreq>" + "<ver>" + ver + "</ver>" + "<merdt>" + merdt + "</merdt>" + "<orderno>" + orderno + "</orderno>" + "<bankno>" + bankno + "</bankno>" + "<cityno>" + cityno + "</cityno>" + "<branchnm>" + branchnm + "</branchnm>" + "<accntno>" + accntno + "</accntno>" + "<accntnm>" + accntnm + "</accntnm>" + "<amt>" + amt + "</amt><mobile>" + mobile + "</mobile></payforreq>";
            String macSource = merchantNumber + "|" + password + "|" + "payforreq" + "|" + xml
            String mac = md5Service.encode(macSource, "UTF-8").toUpperCase();
            String loginUrl = "https://fht.fuiou.com/req.do";
            def params = "merid=" + merchantNumber + "&reqtype=" + reqtype + "&xml=" + xml + "&mac=" + mac
            def resultStr = requestPost(loginUrl, params);
            def rootNode = new XmlParser().parseText(resultStr)
            payment.responseCode = rootNode.ret.text()
            payment.responseDescribe = rootNode.memo.text()
            if(payment.validate()){
                payment.save flush:true
            } else {
                println payment.errors
            }
            if(rootNode.ret.text()=="000000"){
                render model :[code : 100 ,status : "代付成功"]
            }else {
                render model :[code : 500 ,status : rootNode.memo.text()]
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // 代扣 代收  需要项目录入填写项目ID
    def makeCollections(idNumber,numberOfAccount,fullName,mobileNo,project_id,bankName,amount)
    {
        def makeCollections = new MakeCollectionsInformation()
        try
        {
            def ver = version
            //版本号
            def merdt = new Date().format("yyyyMMdd")
            makeCollections.requestDate = merdt
            //请求日期
            def orderno = System.currentTimeMillis()
            makeCollections.serialNumber = orderno
            //请求流水
            def bankno = Bank.findByName(bankName).code
            println bankno+"bankno"
            makeCollections.bankCode = bankno
            //总行代码
            def accntno = numberOfAccount
            makeCollections.numberOfAccount = accntno
            //账号
            def accntnm = fullName
            //账户名称
            def amt = amount
            makeCollections.amount = amt
            //金额
            def mobile = mobileNo
            //手机号
            def certtp = "0"
            //  证件类型   发送交易到银行时用来做校验
            def certno = idNumber
            // 证件号
            def txncd = "09"
            // 业务定义：贷款还款、逾期还款、债权转让、其他；（代收付 2.0 必填
            def projectid = project_id
            //user.project.projectId
            //业务规则项目 id（代收付 2.0 必填）
            def merid = merchantNumber
            def reqtype = "sincomeforreq"
            String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>" + "<incomeforreq><ver>" + ver + "</ver>" + "<merdt>" + merdt + "</merdt>" + "<orderno>" + orderno + "</orderno>" + "<bankno>" + bankno + "</bankno>" + "<accntno>" + accntno + "</accntno>" + "<accntnm>" + accntnm + "</accntnm>" + "<amt>" + amt + "</amt>" + "<entseq>test</entseq>" + "<memo>备注</memo>" + "<mobile>" + mobile + "</mobile>" + "<certtp>" + certtp + "</certtp>" + "<certno>" + certno + "</certno>" + "<txncd>" + txncd + "</txncd>" + "<projectid>" + projectid + "</projectid>" + "</incomeforreq>";
            String macSource = merchantNumber + "|" + password + "|" + "sincomeforreq" + "|" + xml;
            String mac = md5Service.encode(macSource, "UTF-8").toUpperCase();
            String loginUrl = "https://fht.fuiou.com/req.do";
            def params = "merid=" + merid + "&reqtype=" + reqtype + "&xml=" + xml + "&mac=" + mac
            def resultStr = requestPost(loginUrl, params);
            def rootNode = new XmlParser().parseText(resultStr)
            makeCollections.responseCode = rootNode.ret.text()
            makeCollections.responseDescribe = rootNode.memo.text()
            if(makeCollections.validate()){
                makeCollections.save flush:true
            } else {
               println makeCollections.errors
            }
            def map = [:]
            println rootNode.ret.text()
            if(rootNode.ret.text()=="000000"){
                map.put("code",400)
                return JsonOutput.toJson(map).toString()
            } else {
                map.put("code",300)
                map.put("desc",makeCollections.responseDescribe)
                return JsonOutput.toJson(map).toString()
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //交易查询
    def transactionQuery()
    {
        try
        {
            def ver = "1.00"
            //版本号
            def busicd = params.busicd
            //三种  代收 代付 退票
            //业务代码
            def orderno = ""
            //原请求流水
            def startdt = params.startTime
            startdt = startdt.replace("-", "")
            //开始日期
            def enddt = params.endTime
            enddt = enddt.replace("-", "")
            //结束日期
            def transst = ""
            //交易状态
            String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>" + "<qrytransreq>" + "<ver>" + ver + "</ver>" + "<busicd>" + busicd + "</busicd>" + "<orderno>" + orderno + "</orderno>" + "<startdt>" + startdt + "</startdt>" + "<enddt>" + enddt + "</enddt>" + "<transst>" + transst + "</transst>" + "</qrytransreq>";
            String macSource = merchantNumber + "|" + password + "|" + "qrytransreq" + "|" + xml;
            def merid = "0002900F0345142"
            def reqtype = "qrytransreq"
            String mac = md5Service.encode(macSource, "UTF-8").toUpperCase();
            String loginUrl = "https://fht.fuiou.com/req.do";
            def params = "merid=" + merid + "&reqtype=" + reqtype + "&xml=" + xml + "&mac=" + mac

            def resultStr = requestPost(loginUrl, params)
            def rootNode = new XmlParser().parseText(resultStr)
            def a = []
            rootNode.trans.each {
                a.add(it.orderno.text())
            }
            render rootNode.memo.text() + a
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //退票接口（代收付）回调
    def returnTicket()
    {
        /*0  交易未发送
        1  交易已发送且成功
        2  交易已发送且失败
        3  交易发送中
        7  交易已发送且超时*/
        println "退票接口"
        def ticket = new ReturnTicket()
        def orderno = request.getParameter("orderno")
        //商户请求流水
        ticket.serialNumber = orderno
        def merdt = request.getParameter("merdt")
        //原请求日期
        ticket.requestDate = merdt
        def fuorderno = request.getParameter("fuorderno")
        //富友流水
        ticket.fuiouSerialNumber = fuorderno
        def tpmerdt = request.getParameter("tpmerdt")
        //退票日期
        ticket.returnDate = tpmerdt
        def accntno = request.getParameter("accntno")
        //账号
        ticket.bankCardNumber = accntno
        def accntnm = request.getParameter("accntnm")
        //账户名称
        ticket.fullName = accntnm
        def bankno = request.getParameter("bankno")
        //总行代码
        ticket.bankCode = bankno
        def amt = request.getParameter("amt")
        //退票金额
        ticket.amount = amt
        def state = request.getParameter("state")
        //状态  1
        ticket.state = state
        def result = request.getParameter("result")
        //交易结果
        ticket.result = result
        def reason = request.getParameter("reason")
        //结果原因
        ticket.reason = reason
        def mac = request.getParameter("mac")
        //校验值

        def macSource = merchantNumber + "|" + password + "|" + orderno + "|" + merdt + "|" + accntno + "|" + amt
        macSource = md5Service.encode(macSource, "UTF-8").toUpperCase();
        if (macSource == mac)
        {
            if(ticket.validate()){
                ticket.save flush:true
            } else {
                println ticket.errors
            }
        }
    }

    def test(){
        def amt = 3.14E-6
        int amount = amt * 10000 * 100
        println amount
        int a =new  BigDecimal(amt * 10000 * 100 ).setScale(0,BigDecimal.ROUND_HALF_UP).toInteger()
        println a
    }
}