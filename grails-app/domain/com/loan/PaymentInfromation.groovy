package com.loan

class PaymentInfromation
{
    String numberOfAccount
    // 卡号
    String requestDate
    //请求日期
    String serialNumber
    //请求流水号
    String amount
    //金额
    String responseCode
    //响应码
    String responseDescribe
    //响应描述
    Date createdDate = new Date()
    static constraints = {
        numberOfAccount maxSize: 30
        requestDate maxSize: 8
        serialNumber maxSize: 32
        amount maxSize: 16
        responseCode nullable: true, blank: true ,maxSize: 8
        responseDescribe nullable: true, blank: true, maxSize: 128
    }
}
