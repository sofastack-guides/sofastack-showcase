package com.aliyun.gts.financial.showcases.sofa.facade.result;

import java.util.Date;

public class AccountTransResult extends AbstractAccountResult {

    /** 序列号 */
    private static final long serialVersionUID = -6012421796634675727L;

    /**
     * 交易凭证流水号
     */
    private String            transLogId;

    /**
     * 交易时间
     */
    private Date              transDt;


    public AccountTransResult() {
    }


    public AccountTransResult(boolean isSuccess, String msgCode, String desc) {
        this.success = isSuccess;
        this.setMsgCode(msgCode);
        this.setMsgText(desc);
    }


    /**
     * 静态工厂方法
     * 通过ErrorContext构建结果对象
     * @return 返回结果类型
     */
    public static  AccountTransResult failedResultOf( String msgCode,String desc) {
        return new AccountTransResult (false, msgCode, desc);

    }

    /**
     * Getter method for property <tt>transLogId</tt>.
     * 
     * @return property value of transLogId
     */
    public String getTransLogId() {
        return transLogId;
    }

    /**
     * Setter method for property <tt>transLogId</tt>.
     * 
     * @param transLogId value to be assigned to property transLogId
     */
    public void setTransLogId(String transLogId) {
        this.transLogId = transLogId;
    }

    /**
     * Getter method for property <tt>transDt</tt>.
     * 
     * @return property value of transDt
     */
    public Date getTransDt() {
        return transDt;
    }

    /**
     * Setter method for property <tt>transDt</tt>.
     * 
     * @param transDt value to be assigned to property transDt
     */
    public void setTransDt(Date transDt) {
        this.transDt = transDt;
    }

}