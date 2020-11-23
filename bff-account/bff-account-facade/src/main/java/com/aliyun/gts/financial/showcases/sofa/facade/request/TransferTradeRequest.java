package com.aliyun.gts.financial.showcases.sofa.facade.request;

import java.math.BigDecimal;

public class TransferTradeRequest {

    private String requestId;

    private String fromAccountNo;

    private String toAccountNo;

    private BigDecimal transferAmount;

    public String getFromAccountNo() {
        return this.fromAccountNo;
    }

    public void setFromAccountNo(String fromAccountNo) {
        this.fromAccountNo = fromAccountNo;
    }

    public String getToAccountNo() {
        return this.toAccountNo;
    }

    public void setToAccountNo(String toAccountNo) {
        this.toAccountNo = toAccountNo;
    }

    public BigDecimal getTransferAmount() {
        return this.transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

}