package com.loan

class ResponseCode
{
    String code
    String description
    static constraints = {
        code maxSize: 4
        description maxSize: 16
    }
}
