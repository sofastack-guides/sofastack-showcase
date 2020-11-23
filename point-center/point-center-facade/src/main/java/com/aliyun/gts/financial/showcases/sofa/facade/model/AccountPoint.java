package com.aliyun.gts.financial.showcases.sofa.facade.model;

/**
 * 账户积分
 */
public class AccountPoint {

    /**
     * 账户
     */
    private String accountNo;

    /**
     * 积分
     */
    private double point;

    /**
     * 状态，用于tcc场景
     */
    private int status = 0;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}