package com.aliyun.gts.financial.showcases.sofa.tcc;

import com.alipay.dtx.client.core.api.domain.BusinessActionContext;
import com.alipay.sofa.dtx.rm.tcc.TccTransactionController;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.aliyun.gts.financial.showcases.sofa.facade.model.Account;
import com.aliyun.gts.financial.showcases.sofa.facade.request.AccountTransRequest;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountTransResult;
import com.aliyun.gts.financial.showcases.sofa.facade.tcc.AcctDrawTccService;
import com.aliyun.gts.financial.showcases.sofa.template.BizCallback;
import com.aliyun.gts.financial.showcases.sofa.template.BizTemplate;

import org.springframework.transaction.TransactionStatus;

@SofaService(bindings = { @SofaServiceBinding(bindingType = "bolt") })
public class AcctDrawTccServiceImpl extends AcctAbstractTccService implements AcctDrawTccService {

    @Override
    public AccountTransResult debit(String uid, AccountTransRequest accountTransRequest, String shardingKey,
            BusinessActionContext businessActionContext) {

        return BizTemplate.executeWithTransaction(accountTransactionTemplate, new BizCallback() {

            @Override
            public void execute(TransactionStatus status) {
                // 防悬挂
                TccTransactionController.doAntiSuspendControl();

                // 记录事务与业务关系，用于二阶段根据事务获取业务信息，以及二阶段的幂等性验证
                connectTxWithBusiness(businessActionContext, accountTransRequest, shardingKey, false);

                // 转账场景下，扣钱是针对发起账户
                Account account = getAccountAndCheck(accountTransRequest.getBacc());

                // 增加冻结金额
                increaseFreezeAmount(account, accountTransRequest.getTxnAmt());

                // 检查余额是否满足
                checkBalance(account);
            }

            @Override
            public void checkParameter() {
            }
        });
    }

    @Override
    public boolean commit(BusinessActionContext businessActionContext) {
        return super.commit(businessActionContext, false);
    }

    @Override
    public boolean rollback(BusinessActionContext businessActionContext) {
        return super.rollback(businessActionContext, false);
    }

}