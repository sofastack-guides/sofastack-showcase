package com.aliyun.gts.financial.showcases.sofa.tcc;

import com.alipay.dtx.client.core.api.domain.BusinessActionContext;
import com.alipay.sofa.dtx.rm.tcc.TccTransactionController;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.aliyun.gts.financial.showcases.sofa.facade.model.Account;
import com.aliyun.gts.financial.showcases.sofa.facade.request.AccountTransRequest;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountTransResult;
import com.aliyun.gts.financial.showcases.sofa.facade.tcc.AcctDepositTccService;
import com.aliyun.gts.financial.showcases.sofa.template.BizCallback;
import com.aliyun.gts.financial.showcases.sofa.template.BizTemplate;

import org.springframework.transaction.TransactionStatus;

@SofaService(bindings = {@SofaServiceBinding(bindingType = "bolt")})
public class AcctDepositTccServiceImpl extends AcctAbstractTccService implements AcctDepositTccService {

    @Override
    public AccountTransResult credit(String uid, AccountTransRequest accountTransRequest, String shardingKey,
                                     BusinessActionContext businessActionContext) {

        return BizTemplate.executeWithTransaction(accountTransactionTemplate, new BizCallback() {

            @Override
            public void execute(TransactionStatus status) {
                // 防悬挂
                TccTransactionController.doAntiSuspendControl();

                // 记录事务与业务关系，用于二阶段根据事务获取业务信息，以及二阶段的幂等性验证
                connectTxWithBusiness(businessActionContext, accountTransRequest, shardingKey, true);

                // 转账场景下，加钱是给对方账户
                Account account = getAccountAndCheck(accountTransRequest.getPeerBacc());

                // 增加未到达金额
                increaseUnreachAmount(account, accountTransRequest.getTxnAmt());
            }

            @Override
            public void checkParameter() {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public boolean commit(BusinessActionContext businessActionContext) {
        return super.commit(businessActionContext, true);
    }

    @Override
    public boolean rollback(BusinessActionContext businessActionContext) {
        return super.rollback(businessActionContext, true);
    }

}