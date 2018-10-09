<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>5要素接口页面 </title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
    <g:javascript>
        function save() {
            $.ajax({
                type: "post",
                url: "fiveElementSign",
                data: {
                    "srcChnl": $("#srcChnl").val(),
                    "busiCd": $("#busiCd").val(),
                    "bankCd": $("#bankCd").val(),
                    "userNm": $("#userNm").val(),
                    "mobileNo": $("#mobileNo").val(),
                    "credtTp": $("#credtTp").val(),
                    "credtNo": $("#credtNo").val(),
                    "acntTp": $("#acntTp").val(),
                    "acntNo": $("#acntNo").val(),
                    "mchntCd": $("#mchntCd").val(),
                    "isCallback": $("#isCallback").val(),
                    "reserved1": $("#reserved1").val(),
                    "pageFrontUrl": $("#pageFrontUrl").val()
                },
                dataType: "json",
                success: function (data) {
                    alert(data.signature)
                    $("#key").val(data.signature);

                    $("#form").submit()
                }
            });

        }

    </g:javascript>
</head>
<body>
<h1 style="margin-left: 450px">5要素接口页面</h1>
<div style="margin-left: 400px;margin-top: 100px">
    <form method="post" id="form" action="https://fht-test.fuiou.com/fuMer/api_contractAPP.do">
        <table>

            <tr>
                <td>行别:</td>
                    <td>
                        <select  name="bankCd"  id="bankCd" style="width: auto">
                            <option value="0102">中国工商银行</option>
                            <option value="0103">中国农业银行</option>
                            <option value="0105">中国建设银行</option>
                            <option value="0104" >中国银行</option>
                            <option value="0303" >光大银行</option>
                    </select>
                    </td>
            </tr>
            <tr>
                <td>户名:</td>
                <td><input type="text" size="20" name="userNm"   id="userNm"></td>
            </tr>
            <tr>
                <td>手机号:</td>
                <td><input type="text" size="20" name="mobileNo"  id="mobileNo"></td>
            </tr>

            <tr>
                <td>身份证号码:</td>
                <td><input type="text" size="20" name="credtNo"  id="credtNo"></td>
            </tr>

            <tr>
                <td>银行卡号:</td>
                <td><input type="text" size="20" name="acntNo"  id="acntNo"></td>
            </tr>

            <tr>
                <td>备注:</td>
                <td><input type="text" size="20" name="reserved1"  id="reserved1"></td>
            </tr>

            %{--签约来源 APP--}%
            <input type="hidden" size="20" name="srcChnl"  id="srcChnl" value="APP">

            %{--业务来源 AC01--}%
            <input type="hidden" size="20" name="busiCd" id="busiCd" value="AC01">

            %{--身份证--}%
            <input type="hidden" size="20" name="credtTp"  id="credtTp" value="0">

            %{--借记卡--}%
            <input type="hidden" size="20" name="acntTp"   id="acntTp" value="01">

            %{--<td>商户号:</td>--}%
            <input type="hidden" size="20" name="mchntCd"  id="mchntCd" value="0002900F0345142">

            %{--<tr>
                <td>回拨号码:</td>
                <td><input type="hidden" size="20" name="isCallback"  id="isCallback" value="0"></td>
            </tr>--}%
            <input type="hidden" size="20" name="isCallback"  id="isCallback" value="0">

            %{-- <tr>
                <td>返回地址:</td>
                <td><input type="hidden" size="20" name="pageFrontUrl"  id="pageFrontUrl" value="localhost:8080"></td>
            </tr>--}%

            <input type="hidden" size="20" name="pageFrontUrl"  id="pageFrontUrl" value="http://localhost:8080/payment/project">

            %{-- <td>签名:</td>--}%
            <input type="hidden" size="20" name="signature" id="key">

            <tr>
                <td>&nbsp;</td><td><input type="button" onclick="save()" value="提交" id = "boot"></td>
            </tr>
        </table>
    </form>
    </div>
</body>

</html>
