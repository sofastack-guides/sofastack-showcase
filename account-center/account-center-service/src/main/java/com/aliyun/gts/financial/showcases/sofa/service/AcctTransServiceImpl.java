package com.aliyun.gts.financial.showcases.sofa.service;

import com.alipay.sofa.dtx.client.aop.annotation.DtxTransaction;
import com.aliyun.gts.financial.showcases.sofa.facade.api.AcctTransService;
import com.aliyun.gts.financial.showcases.sofa.enums.CodeEnum;
import com.aliyun.gts.financial.showcases.sofa.facade.fmt.AcctDepositFmtService;
import com.aliyun.gts.financial.showcases.sofa.facade.fmt.AcctDrawFmtService;
import com.aliyun.gts.financial.showcases.sofa.facade.request.AccountTransRequest;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountTransResult;
import com.aliyun.gts.financial.showcases.sofa.facade.tcc.AcctDepositTccService;
import com.aliyun.gts.financial.showcases.sofa.facade.tcc.AcctDrawTccService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AcctTransServiceImpl implements AcctTransService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AcctTransServiceImpl.class);

    @Autowired
    AcctDrawFmtService acctDrawFmtService;

    @Autowired
    AcctDepositFmtService acctDepositFmtService;

    @Autowired
    AcctDepositTccService acctDepositTccService;

    @Autowired
    AcctDrawTccService acctDrawTccService;

    @Override
    @DtxTransaction(bizType = "transfer-by-fmt")
    public AccountTransResult transerByFmt(AccountTransRequest accountTransRequest) {
        AccountTransResult accountTransResult = null;
        accountTransResult = acctDrawFmtService.debit(accountTransRequest);
        if (!accountTransResult.isSuccess()) {
            // 事务回滚
            throw new RuntimeException("debit fmt failed.");
        }
        accountTransResult = acctDepositFmtService.credit(accountTransRequest);
        if (!accountTransResult.isSuccess()) {
            // 事务回滚
            throw new RuntimeException("credit fmt failed.");
        }
        accountTransResult.setMsgCode(CodeEnum.SUCCESS.getCode());
        accountTransResult.setMsgText(CodeEnum.SUCCESS.getDesc());
        return accountTransResult;
    }

    @Override
    @DtxTransaction(bizType = "transfer-by-tcc")
    public AccountTransResult transerByTcc(AccountTransRequest accountTransRequest) {
        LOGGER.info("transfer by tcc started......");
        AccountTransResult accountTransResult = null;

        // 转出
        accountTransResult = acctDrawTccService.debit(accountTransRequest, accountTransRequest.getBacc(), null);
        if (!accountTransResult.isSuccess()) {
            throw new RuntimeException("debit tcc failed.");
        }

        // 转入
        accountTransResult = acctDepositTccService.credit(accountTransRequest, accountTransRequest.getPeerBacc(), null);
        if (!accountTransResult.isSuccess()) {
            throw new RuntimeException("credit tcc failed.");
        }
        return accountTransResult;
    }

}