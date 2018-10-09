package com.loan

/*
* author : yuanchao
* 用户的银行卡账户的基本信息
* */

class UserAccountInformation {
    String fullName
    //客户姓名
    String idNumber
    //身份证号
    String numberOfAccount
    // 卡号
    String cellphone
    // 银行预留手机号
    String responseCode
    //响应码
    String responseDesc
    //响应描述
    City city
    //城市
    Date createdDate = new Date()
    //记录创建时间
    static constraints = {
        fullName maxSize: 32
        idNumber maxSize: 18
        numberOfAccount maxSize: 24
        cellphone matches: /^1\d{10}$/
        responseCode maxSize: 8, nullable: true, blank: true
        responseDesc maxSize: 32, nullable: true, blank: true
        city nullable: true, blank: true
    }
}
