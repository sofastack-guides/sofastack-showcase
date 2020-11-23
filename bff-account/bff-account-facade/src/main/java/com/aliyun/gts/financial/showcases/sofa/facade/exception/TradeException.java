package com.aliyun.gts.financial.showcases.sofa.facade.exception;

public class TradeException extends Exception{

    /***
     * You should define you errorCode here, this is only an example
     */
    private String errorCode = "TradeError";

    public TradeException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
