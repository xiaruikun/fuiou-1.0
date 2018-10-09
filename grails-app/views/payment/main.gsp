<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>支付页面 </title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>
<h1 style="margin-left: 450px">支付页面</h1>
<div style="margin-left: 400px;margin-top: 100px">
    <form method="post" id="form" action="transactionQuery">
        开始时间:<input type="date" name="startTime" />
        结束时间:<input type="date" name="endTime"  />
        业务:<select  name="busicd">
        <option value="AC01">代收</option>
        <option value="AP01">代付</option>
        <option value="TO01">退票</option>
    </select>
        <input type="submit" value="查询">
    </form>
    <a href="payForCollection">代付</a>
    <a href="makeCollections">代收</a>
    </div>
</body>

</html>
