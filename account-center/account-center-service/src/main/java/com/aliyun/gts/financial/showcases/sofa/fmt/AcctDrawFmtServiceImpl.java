package com.aliyun.gts.financial.showcases.sofa.fmt;

import java.math.BigDecimal;

import com.aliyun.gts.financial.showcases.sofa.dao.AccountDAO;
import com.aliyun.gts.financial.showcases.sofa.enums.CodeEnum;
import com.aliyun.gts.financial.showcases.sofa.exception.AcctCenterException;
import com.aliyun.gts.financial.showcases.sofa.facade.fmt.AcctDrawFmtService;
import com.aliyun.gts.financial.showcases.sofa.facade.model.Account;
import com.aliyun.gts.financial.showcases.sofa.facade.request.AccountTransRequest;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountTransResult;
import com.aliyun.gts.financial.showcases.sofa.template.BizCallback;
import com.aliyun.gts.financial.showcases.sofa.template.BizTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

public class AcctDrawFmtServiceImpl implements AcctDrawFmtService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AcctDrawFmtServiceImpl.class);

    @Autowired
    private AccountDAO accountDAOFmt;

    @Autowired
    private TransactionTemplate accountTransactionTemplateFmt;

    @Override
    public AccountTransResult debit(AccountTransRequest accountTransRequest) {

        return BizTemplate.executeWithTransaction(accountTransactionTemplateFmt, new BizCallback() {

            @Override
            public void execute(TransactionStatus status) {
                // 转账场景下，扣钱是针对发起账户
                String targetAccount = accountTransRequest.getBacc();
                BigDecimal targetAmount = accountTransRequest.getTxnAmt();
                LOGGER.info("balance_subtract getAccountForUpdate {}", targetAccount);
                Account account = accountDAOFmt.getAccountForUpdate(targetAccount);

                // 检查账户
                if (account == null) {
                    throw new AcctCenterException(CodeEnum.ACCOUNT_NULL);
                }

                // 检查余额
                BigDecimal newAmount = account.getBalance().subtract(targetAmount);
                if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
                    throw new AcctCenterException(CodeEnum.ACCOUNT_BALANCE_NOT_ENOUGH);
                }

                // 扣钱
                account.setBalance(newAmount);
                accountDAOFmt.updateBalance(account);
                LOGGER.info("balance_subtract updateAmount {}", targetAccount);
            }

            @Override
            public void checkParameter() {
                // TODO Auto-generated method stub

            }
        });
    }

}