package com.aliyun.gts.financial.showcases.sofa.enums;

public enum CodeEnum {

    SUCCESS("000", "SUCCESS"),

    /** 未知异常 */
    UNKNOWN_EXCEPTION("999", "UNKNOWN_EXCEPTION"),

    /** 系统异常 */
    SYSTEM_EXCEPTION("999999" , "SYSTEM_EXCEPTION"),

    /** 账户状态错误 */
    ACCOUNT_STATUS_ERROR("000010", "ACCOUNT_STATUS_ERROR"),

    /** DB异常 */
    DB_EXCEPTION("888888", "DB_EXCEPTION"),

    /** 幂等异常 */
    IDEMPOTENT_EXCEPTION("777777", "DB_EXCEPTION"),

    /** 账户为空 */
    ACCOUNT_NULL("100000", "ACCOUNT_NO_NULL"),

    /** 账户余额不足 */
    ACCOUNT_BALANCE_NOT_ENOUGH("000011", "ACCOUNT_BALANCE_NOT_ENOUGH"),

    /** 转账异常 */
    TRANSFER_EXCEPTION("007", "TRANSFER_EXCEPTION"),

    /** 交易账号繁忙 */
    TRANS_ACCOUNT_BUZY( "008", "交易账号繁忙");

    /** 枚举码 */
    private final String code;

    /** 枚举码描述 */
    private final String desc;

    /**
     * 构造器
     *
     * @param code          枚举码
     * @param desc          枚举码描述
     */
    CodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    //~~~~~属性方法~~~~~

    /**
     * Getter method for property <tt>code</tt>.
     *
     * @return property value of code
     */
    public String getCode() {
        return code;
    }

    /**
     * Getter method for property <tt>desc</tt>.
     *
     * @return property value of desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Get enum name
     *
     * @return String    name
     */
    public String getName() {
        return name();
    }
}