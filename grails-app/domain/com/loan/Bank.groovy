package com.loan

class Bank
{
    String name
    String code
    static constraints = {
        name maxSize: 16
        code maxSize: 4
    }
}
