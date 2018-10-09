<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>项目录入页面 </title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>
<h1 style="margin-left: 450px">项目录入页面</h1>
<div style="margin-left: 400px;margin-top: 100px">
    <form method="post" id="form" action="projectEntry">
        <table>
            <tr>
                <td>商户名称:</td>
                <td><input type="text" size="20" name="mchnt_nm"   id="mchnt_nm"></td>
            </tr>
            <tr>
                <td>项目金额:</td>
                <td><input type="text" size="20" name="project_amt"  id="project_amt"></td>
            </tr>

            <tr>
                <td>商户借款合同号:</td>
                <td><input type="text" size="20" name="contract_nm"  id="contract_nm"></td>
            </tr>

            <tr>
            <td>贷款期限:</td>
            <td><input type="text" size="20" name="project_deadline"  id="project_deadline"></td>
        </tr>

            <tr>
                <td>借款人姓名:</td>
                <td><input type="text" size="20" name="full_name"  id="a"></td>
            </tr>

            <tr>
                <td>借款人身份证号码:</td>
                <td><input type="text" size="20" name="id_number"  id=""></td>
            </tr>

            <tr>
                <td>&nbsp;</td><td><input type="submit"  value="提交" ></td>
            </tr>
        </table>
    </form>
    </div>
</body>

</html>
