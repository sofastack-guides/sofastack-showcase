package com.aliyun.gts.financial.showcases.sofa.exception;

import com.aliyun.gts.financial.showcases.sofa.enums.CodeEnum;

public class AcctCenterException extends RuntimeException {

    /** 异常错误码 */
    private final CodeEnum errorCode;

    /** 异常堆栈 */
    private Throwable           exception;

    /** 业务异常描述 */
    private String              errorDesc;

    /**
     * 构造函数
     * 
     * @param errorCode         异常错误码
     */
    public AcctCenterException(CodeEnum errorCode) {
        super(errorCode.getCode());
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     * 
     * @param errorCode         异常错误码
     * @param exception         异常堆栈
     */
    public AcctCenterException(CodeEnum errorCode, Throwable exception) {
        super(errorCode.getCode(), exception);
        this.errorCode = errorCode;
        this.exception = exception;
    }

    /**
     * 构造函数
     * 
     * @param errorCode         异常错误码
     * @param errorDesc         业务异常描述
     */
    public AcctCenterException(CodeEnum errorCode, String errorDesc) {
        super();
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public AcctCenterException(CodeEnum errorCode, String errorDesc, Throwable cause) {
        super(errorCode.getCode(), cause);
        this.errorDesc = errorDesc;
        this.exception = cause;
        this.errorCode = errorCode;
    }

    //~~~~~属性方法~~~~~

    /**
     * Getter method for property <tt>errorCode</tt>.
     * 
     * @return property value of errorCode
     */
    public CodeEnum getErrorCode() {
        return errorCode;
    }

    /**
     * Getter method for property <tt>exception</tt>.
     * 
     * @return property value of exception
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * Getter method for property <tt>errorDesc</tt>.
     * 
     * @return property value of errorDesc
     */
    public String getErrorDesc() {
        return errorDesc;
    }

}