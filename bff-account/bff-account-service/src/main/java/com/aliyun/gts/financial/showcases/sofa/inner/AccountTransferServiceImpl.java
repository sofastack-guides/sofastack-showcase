package com.aliyun.gts.financial.showcases.sofa.inner;

import com.alipay.dtx.client.core.api.DtxService;
import com.alipay.sofa.dtx.client.aop.annotation.DtxTransaction;
import com.alipay.sofa.dtx.client.context.RuntimeContext;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.aliyun.gts.financial.showcases.sofa.facade.inner.AccountTransferService;
import com.aliyun.gts.financial.showcases.sofa.facade.request.AccountTransRequest;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountTransResult;
import com.aliyun.gts.financial.showcases.sofa.facade.tcc.AcctDepositTccService;
import com.aliyun.gts.financial.showcases.sofa.facade.tcc.AcctDrawTccService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@SofaService(bindings = { @SofaServiceBinding(bindingType = "bolt") })
public class AccountTransferServiceImpl implements AccountTransferService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AccountTransferServiceImpl.class);

    // RPC演示：注解方式引用bolt协议的RPC服务
    @SofaReference(interfaceType = AcctDepositTccService.class, binding = @SofaReferenceBinding(bindingType = "bolt"))
    private AcctDepositTccService acctDepositTccService;

    @SofaReference(interfaceType = AcctDrawTccService.class, binding = @SofaReferenceBinding(bindingType = "bolt"))
    private AcctDrawTccService acctDrawTccService;

    // DTX演示：TCC模式，发起者
    @Override
    @DtxTransaction(bizType = "transfer-by-tcc")
    public AccountTransResult transerByTcc(AccountTransRequest accountTransRequest) {
        LOGGER.info("transfer by tcc started......");
        AccountTransResult accountTransResult = null;

        // 转出
        accountTransResult = acctDrawTccService.debit(accountTransRequest, accountTransRequest.getBacc(), null);
        if (!accountTransResult.isSuccess()) {
            LOGGER.error("debit tcc failed: {}", accountTransResult.getErrorMessage());
            throw new RuntimeException("debit tcc failed.");
        }

        // 转入
        accountTransResult = acctDepositTccService.credit(accountTransRequest, accountTransRequest.getPeerBacc(), null);
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