package com.aliyun.gts.financial.showcases.sofa.inner;

import com.alipay.dtx.client.core.api.DtxService;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.aliyun.gts.financial.showcases.sofa.facade.inner.AccountTransferService;
import com.aliyun.gts.financial.showcases.sofa.facade.request.AccountTransRequest;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountTransResult;
import com.aliyun.gts.financial.showcases.sofa.facade.tcc.AcctDepositTccService;
import com.aliyun.gts.financial.showcases.sofa.facade.tcc.AcctDrawTccService;
import com.aliyun.gts.financial.showcases.sofa.utils.UIDUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountTransferServiceImpl implements AccountTransferService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AccountTransferServiceImpl.class);

    @SofaReference(interfaceType = AcctDepositTccService.class, binding = @SofaReferenceBinding(bindingType = "bolt"))
    private AcctDepositTccService acctDepositTccService;

    @SofaReference(interfaceType = AcctDrawTccService.class, binding = @SofaReferenceBinding(bindingType = "bolt"))
    private AcctDrawTccService acctDrawTccService;

    @Autowired
    private DtxService dtxService;

    // DTX：分布式事务TCC模式
    @Override
    //@DtxTransaction(bizType = "transfer-by-tcc")
    public AccountTransResult transerByTcc(AccountTransRequest accountTransRequest, String streamId) {
        LOGGER.info("transfer by tcc started......");
        AccountTransResult accountTransResult = null;

        String fromAccountNo = accountTransRequest.getBacc();
        String toAccountNo = accountTransRequest.getPeerBacc();
        String fromUid = UIDUtil.extractUidFromAccountNo(fromAccountNo);
        String toUid = UIDUtil.extractUidFromAccountNo(toAccountNo);

        // biztype只能是数字字母和下划线,"^[A-Za-z0-9_]{1,30}$"
        dtxService.start("transfer_by_tcc", streamId, fromUid, null);
        // 转出
        accountTransResult = acctDrawTccService.debit(fromUid, accountTransRequest, fromAccountNo, null);
        if (!accountTransResult.isSuccess()) {
            LOGGER.error("debit tcc failed: {}", accountTransResult.getErrorMessage());
            throw new RuntimeException("debit tcc failed.");
        }

        // 转入
        accountTransResult = acctDepositTccService.credit(toUid, accountTransRequest, toAccountNo, null);
        if (!accountTransResult.isSuccess()) {
            LOGGER.error("credit tcc failed: {}", accountTransResult.getErrorMessage());
            throw new RuntimeException("credit tcc failed.");
        }

        return accountTransResult;
    }

    @Override
    public AccountTransResult transerByFmt(AccountTransRequest accountTransRequest) {
        // TODO Auto-generated method stub
        return null;
    }

}