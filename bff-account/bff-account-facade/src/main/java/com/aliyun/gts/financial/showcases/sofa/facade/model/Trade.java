package com.aliyun.gts.financial.showcases.sofa.facade.model;

import java.util.Date;

public class Trade {

    private String tradeNo;

    private Date tradeTime;

    private String status;

    public String getTradeNo() {
        return this.tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public Date getTradeTime() {
        return this.tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}