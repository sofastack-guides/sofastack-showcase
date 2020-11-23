package com.aliyun.gts.financial.showcases.sofa.facade.model;

import java.math.BigDecimal;

public class Account {

    // 规则：0/1+分布式序列
    private String accountNo;

    private BigDecimal balance;

    private BigDecimal freezeAmount;

    private BigDecimal unreachAmount;

    public String getAccountNo() {
        return this.accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getFreezeAmount() {
        return this.freezeAmount;
    }

    public void setFreezeAmount(BigDecimal freezeAmount) {
        this.freezeAmount = freezeAmount;
    }

    public BigDecimal getUnreachAmount() {
        return this.unreachAmount;
    }

    public void setUnreachAmount(BigDecimal unreachAmount) {
        this.unreachAmount = unreachAmount;
    }
}