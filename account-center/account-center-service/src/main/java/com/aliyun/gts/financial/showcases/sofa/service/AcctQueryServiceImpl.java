package com.aliyun.gts.financial.showcases.sofa.service;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.aliyun.gts.financial.showcases.sofa.dao.AccountDAO;
import com.aliyun.gts.financial.showcases.sofa.facade.api.AcctQueryService;
import com.aliyun.gts.financial.showcases.sofa.enums.CodeEnum;
import com.aliyun.gts.financial.showcases.sofa.facade.model.Account;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountQueryResult;

import org.springframework.beans.factory.annotation.Autowired;

@SofaService(bindings = { @SofaServiceBinding(bindingType = "bolt") })
public class AcctQueryServiceImpl implements AcctQueryService {
    

    @Autowired
    private AccountDAO accountDAO;

    @Override
    public AccountQueryResult queryAccount(String accountNo) {
        AccountQueryResult accountQueryResult = new AccountQueryResult();

        try {
            Account account = accountDAO.getAccount(accountNo);

            if (account == null) {
                accountQueryResult.setSuccess(false);
                accountQueryResult.setMsgCode(CodeEnum.ACCOUNT_NULL.getCode());
                accountQueryResult.setMsgText(CodeEnum.ACCOUNT_NULL.getDesc());
            } else {
                accountQueryResult.setSuccess(true);
                accountQueryResult.setAccount(account);
                accountQueryResult.setMsgCode(CodeEnum.SUCCESS.getCode());
                accountQueryResult.setMsgText(CodeEnum.SUCCESS.getDesc());
            }
        } catch (Exception e) {
            accountQueryResult.setSuccess(false);
            accountQueryResult.setMsgCode(CodeEnum.SYSTEM_EXCEPTION.getCode());
            accountQueryResult.setMsgText(CodeEnum.SYSTEM_EXCEPTION.getDesc());
            accountQueryResult.setErrorMessage(e.getMessage());
        }

        return accountQueryResult;
    }

}