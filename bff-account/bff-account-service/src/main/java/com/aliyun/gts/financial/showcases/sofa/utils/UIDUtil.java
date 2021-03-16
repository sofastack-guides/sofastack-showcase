package com.aliyun.gts.financial.showcases.sofa.utils;


public final class UIDUtil {

    private UIDUtil() {
    }

    /**
     * 
     * @param accountNo
     * @return 账号的前两位作为uid
     */    
    public static String extractUidFromAccountNo(String accountNo) {
        if (accountNo == null) {
            throw new NullPointerException("accountNo is null");
        }

        if (accountNo.trim().isEmpty()) {
            throw new IllegalArgumentException("accountNo is empty");
        }

        return accountNo.substring(0, 2);
    }

}