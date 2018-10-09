package com.loan

class ProjectEntryInformation {
    String numberOfAccount
    //银行卡号
    String serialNumber
    //生产的流水号码（自己生产的标识）
    String projectNumber
    //每天生产的项目序列号(000001  -- 999999)
    String amount
    //项目金额
    String contractNumber
    //合同号
    String deadline
    //项目期限
    String projectId
    //项目 ID
    String responseDescribe
    //响应描述
    String responseCode
    //审核结果
    String remark
    //备注
    String status
    //状态
    Date createdDate = new Date()
    static constraints = {
        numberOfAccount maxSize: 32
        serialNumber maxSize: 32
        projectNumber maxSize: 32
        amount maxSize: 16
        contractNumber maxSize: 16
        deadline maxSize: 8
        projectId maxSize: 32
        responseDescribe maxSize: 128 ,nullable: true, blank: true
        responseCode maxSize: 8 ,nullable: true, blank: true
        status maxSize: 8 ,nullable: true, blank: true
        remark nullable: true, blank: true, maxSize: 8
    }
}
