package com.aliyun.gts.financial.showcases.sofa.facade.model;

import java.math.BigDecimal;
import java.util.Date;

public class TradeDetail {

    /**
     * 流水号
     */
    private String streamId;
    /**
     * 转出账户
     */
    private String custacOut;
    /**
     * 转入账户
     */
    private String custacIn;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 交易日期（YYYYMMDD）
     */
    private Date   tradeDate;

    /**
     * 交易状态
     */
    private String tradeStatus;

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getCustacOut() {
        return custacOut;
    }

    public void setCustacOut(String custacOut) {
        this.custacOut = custacOut;
    }

    public String getCustacIn() {
        return custacIn;
    }

    public void setCustacIn(String custacIn) {
        this.custacIn = custacIn;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

}