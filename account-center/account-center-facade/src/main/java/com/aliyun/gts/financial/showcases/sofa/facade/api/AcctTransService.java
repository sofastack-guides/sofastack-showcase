package com.aliyun.gts.financial.showcases.sofa.facade.api;

import com.aliyun.gts.financial.showcases.sofa.facade.request.AccountTransRequest;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountTransResult;

public interface AcctTransService {
    /**
     * 转账接口 FMT模式
     *
     * @param accountTransRequest 转账请求
     * @return 转账结果
     */
    AccountTransResult transerByFmt(AccountTransRequest accountTransRequest);

    /**
     * 转账接口 TCC模式
     *
     * @param accountTransRequest 转账请求
     * @return 转账结果
     */
    AccountTransResult transerByTcc(AccountTransRequest accountTransRequest);
}