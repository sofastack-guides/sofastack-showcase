package com.aliyun.gts.financial.showcases.sofa.facade.bolt;

import com.aliyun.gts.financial.showcases.sofa.facade.request.AccountTransRequest;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountTransResult;

public interface AccountBoltService {

    AccountTransResult transerTrade(String dtxType, AccountTransRequest accountTransRequest);
}