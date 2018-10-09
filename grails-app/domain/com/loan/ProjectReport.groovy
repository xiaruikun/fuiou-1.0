package com.loan
/**
 * 项目返回报告  异步
 */
class ProjectReport {
    String merchantNumber
    //商户代码
    String serialNumber
    //流水号
    String projectId
    //项目 ID
    String entryTime
    //录入时间
    String result
    //审核结果   0：待审核，1：审核成功， 2：审核失败
    String remark
    //备注
    static constraints = {
        merchantNumber maxSize: 32
        serialNumber maxSize: 30
        projectId maxSize: 50
        entryTime maxSize: 8
        result maxSize: 30
        remark maxSize:  32
    }
}
