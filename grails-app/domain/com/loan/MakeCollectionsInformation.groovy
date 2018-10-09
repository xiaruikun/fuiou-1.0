package com.loan

class MakeCollectionsInformation
{
    String numberOfAccount
    // 卡号
    String bankCode
    //银行编码
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
        numberOfAccount maxSize: 32
         requestDate maxSize: 8
         serialNumber maxSize: 32
         amount maxSize: 16
         responseCode maxSize: 16
         responseDescribe maxSize: 128
    }
}
