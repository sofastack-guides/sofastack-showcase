package com.aliyun.gts.financial.showcases.sofa.facade.request;

import java.math.BigDecimal;
import java.util.Date;

public class AccountTransRequest extends AbstractAccountRequest {

    /** 序列号 */
    private static final long serialVersionUID = -754998616015325640L;

    /**
     * 交易账号 必填
     */
    private String bacc;

    /**
     * 对方账号，可选
     */
    private String peerBacc;

    /**
     * 交易发生金额 必填
     */
    private BigDecimal txnAmt;

    /**
     * 交易流水
     */
    private String txnSn;

    /**
     * 状态
     */
    private String txnStatus;

    /**
     * 交易时间
     */
    private Date txnTime;

    /**
     * Getter method for property <tt>bacc</tt>.
     *
     * @return property value of bacc
     */
    public String getBacc() {
        return bacc;
    }

    /**
     * Setter method for property <tt>bacc</tt>.
     *
     * @param bacc value to be assigned to property bacc
     */
    public void setBacc(String bacc) {
        this.bacc = bacc;
    }

    /**
     * Getter method for property <tt>peerBacc</tt>.
     *
     * @return property value of peerBacc
     */
    public String getPeerBacc() {
        return peerBacc;
    }

    /**
     * Setter method for property <tt>peerBacc</tt>.
     *
     * @param peerBacc value to be assigned to property peerBacc
     */
    public void setPeerBacc(String peerBacc) {
        this.peerBacc = peerBacc;
    }

    /**
     * Getter method for property <tt>txnAmt</tt>.
     *
     * @return property value of txnAmt
     */
    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    /**
     * Setter method for property <tt>txnAmt</tt>.
     *
     * @param txnAmt value to be assigned to property txnAmt
     */
    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    /**
     * Getter method for property <tt>txnSn</tt>.
     *
     * @return property value of txnSn
     */
    public String getTxnSn() {
        return txnSn;
    }

    /**
     * Setter method for property <tt>txnSn</tt>.
     *
     * @param txnSn value to be assigned to property txnSn
     */
    public void setTxnSn(String txnSn) {
        this.txnSn = txnSn;
    }

    /**
     * Getter method for property <tt>txnStatus</tt>.
     *
     * @return property value of txnStatus
     */
    public String getTxnStatus() {
        return txnStatus;
    }

    /**
     * Setter method for property <tt>txnStatus</tt>.
     *
     * @param txnStatus value to be assigned to property txnStatus
     */
    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    /**
     * Getter method for property <tt>txnTime</tt>.
     *
     * @return property value of txnTime
     */
    public Date getTxnTime() {
        return txnTime;
    }

    /**
     * Setter method for property <tt>txnTime</tt>.
     *
     * @param txnTime value to be assigned to property txnTime
     */
    public void setTxnTime(Date txnTime) {
        this.txnTime = txnTime;
    }
}