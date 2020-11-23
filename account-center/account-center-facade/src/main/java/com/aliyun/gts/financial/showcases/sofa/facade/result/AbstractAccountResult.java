package com.aliyun.gts.financial.showcases.sofa.facade.result;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public abstract class AbstractAccountResult implements Serializable {

    /** 序列号 */
    private static final long serialVersionUID = 2859259678877871954L;

    /** 本次处理结果，true表示处理成功，false表示处理失败 */
    protected  boolean success = false;

    /** 标准错误上下文 */
    private String errorMessage;

    /** 错误代码 */
    private String msgCode;

    /** 错误表述 */
    private String msgText;

    //~~~~属性方法~~~~

    /**
     * Getter method for property <tt>success</tt>.
     *
     * @return property value of success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Setter method for property <tt>success</tt>.
     *
     * @param success value to be assigned to property success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Getter method for property <tt>errorMessage</tt>.
     *
     * @return property value of errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Setter method for property <tt>errorMessage</tt>.
     *
     * @param errorMessage value to be assigned to property errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Getter method for property <tt>msgCode</tt>.
     *
     * @return property value of msgCode
     */
    public String getMsgCode() {
        return msgCode;
    }

    /**
     * Setter method for property <tt>msgCode</tt>.
     *
     * @param msgCode  value to be assigned to property msgCode
     */
    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    /**
     * Getter method for property <tt>msgText</tt>.
     *
     * @return property value of msgText
     */
    public String getMsgText() {
        return msgText;
    }

    /**
     * Setter method for property <tt>msgText</tt>.
     *
     * @param msgText  value to be assigned to property msgText
     */
    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}