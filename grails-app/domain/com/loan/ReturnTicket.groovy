package com.loan

class ReturnTicket
{
    String serialNumber
    String requestDate
    //原来的请求日期
    String fuiouSerialNumber
    String returnDate
    //退票日期
    String bankCardNumber
    String fullName
    String bankCode
    String amount
    String state
    String result
    String reason
    Date createdDate = new Date()
    static constraints = {
         serialNumber maxSize: 30
         requestDate maxSize: 8
        //原来的请求日期
         fuiouSerialNumber maxSize: 32
         returnDate maxSize: 16
        //退票日期
         bankCardNumber maxSize: 28
         fullName maxSize: 30
         bankCode maxSize: 4
         amount maxSize: 12
         state maxSize: 2
         result maxSize: 8
         reason maxSize: 1024 ,nullable: true, blank: true
    }
}
