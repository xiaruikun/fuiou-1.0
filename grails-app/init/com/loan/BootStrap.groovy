package com.loan

class BootStrap
{

    def init = { servletContext ->
        /*if (City.count() < 1) {
            println 'init City data'

            def c = new City()
            c.name = "北京"
            c.code = "1000"
            c.save()

        }
        *//*01  借款下发
02  逾期还款
03  债权转让
04  其他
06  贷款还款
07  逾期还款
08  债权转让
09  其他*//*
        if (BusinessDefinition.count() < 1) {
            println 'init BusinessDefinition data'

            def b = new BusinessDefinition()
            b.name = "贷款还款"
            b.code = "06"
            b.save()


            b = new BusinessDefinition()
            b.name = "逾期还款"
            b.code = "07"
            b.save()

            b = new BusinessDefinition()
            b.name = "债权转让"
            b.code = "08"
            b.save()

            b = new BusinessDefinition()
            b.name = "其他"
            b.code = "09"
            b.save()
        }
        *//*0  身份证
        1  护照
        2  军官证
        3  士兵证
        5  户口本
        7  其他*//*
        if (ContactIdentityType.count() < 1) {
            def c = new ContactIdentityType()
            c.name = "身份证"
            c.code = "0"
            c.save()

            c = new ContactIdentityType()
            c.name = "护照"
            c.code = "1"
            c.save()

            c = new ContactIdentityType()
            c.name = "军官证"
            c.code = "2"
            c.save()

            c = new ContactIdentityType()
            c.name = "士兵证"
            c.code = "3"
            c.save()

            c = new ContactIdentityType()
            c.name = "户口本"
            c.code = "5"
            c.save()

            c = new ContactIdentityType()
            c.name = "其他"
            c.code = "7"
            c.save()


        }*/
    }
    def destroy = {}
}
