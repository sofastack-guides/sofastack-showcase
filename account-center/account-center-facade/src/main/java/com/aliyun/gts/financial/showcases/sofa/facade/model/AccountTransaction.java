package com.aliyun.gts.financial.showcases.sofa.facade.model;

import java.math.BigDecimal;

public class AccountTransaction {

    /**
     * 事务id，主健
     */
    private String txId;
    /**
     * 操作账户
     */
    private String accountNo;
    /**
     * 操作金额
     */
    private BigDecimal amount;
    /**
     * 操作
     */
    private String operation;
    /**
     * 当前状态，用于幂等判断
     */
    private String status;
    /**
     * 分库分表字段
     */
    private String shardingKey;

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getShardingKey() {
        return this.shardingKey;
    }

    public void setShardingKey(String shardingKey) {
        this.shardingKey = shardingKey;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}