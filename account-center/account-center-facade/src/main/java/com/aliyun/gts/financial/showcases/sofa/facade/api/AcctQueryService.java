package com.aliyun.gts.financial.showcases.sofa.facade.api;

import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountQueryResult;

public interface AcctQueryService {

    AccountQueryResult queryAccount(String accountNo);

}