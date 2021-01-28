package com.aliyun.gts.financial.showcases.sofa.tcc;

import java.math.BigDecimal;

import com.alipay.dtx.client.core.api.domain.BusinessActionContext;
import com.alipay.dtx.client.core.util.XTSConstant;
import com.aliyun.gts.financial.showcases.sofa.dao.AccountDAO;
import com.aliyun.gts.financial.showcases.sofa.dao.AccountTransactionDAO;
import com.aliyun.gts.financial.showcases.sofa.enums.CodeEnum;
import com.aliyun.gts.financial.showcases.sofa.enums.OperationEnum;
import com.aliyun.gts.financial.showcases.sofa.enums.StatusEnum;
import com.aliyun.gts.financial.showcases.sofa.exception.AcctCenterException;
import com.aliyun.gts.financial.showcases.sofa.facade.model.Account;
import com.aliyun.gts.financial.showcases.sofa.facade.model.AccountTransaction;
import com.aliyun.gts.financial.showcases.sofa.facade.request.AccountTransRequest;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountTransResult;
import com.aliyun.gts.financial.showcases.sofa.template.BizCallback;
import com.aliyun.gts.financial.showcases.sofa.template.BizTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

public abstract class AcctAbstractTccService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcctAbstractTccService.class);

    @Autowired
    protected AccountTransactionDAO accountTransactionDAO;

    @Autowired
    protected AccountDAO accountDAO;

    @Autowired
    protected TransactionTemplate accountTransactionTemplate;

    protected boolean commit(BusinessActionContext businessActionContext, boolean isCredit) {
        AccountTransResult result = BizTemplate.executeWithTransaction(accountTransactionTemplate, new BizCallback() {

            @Override
            public void execute(TransactionStatus status) {
                // 根据txid获取当前交易信息
                AccountTransaction acctTrans = getAccountTransaction(businessActionContext);

                // 幂等性验证
                checkIdempotent(acctTrans, StatusEnum.DONE);

                // 更新余额
                Account account = getAccountAndCheck(acctTrans.getAccountNo());
                if (isCredit) {
                    // 增加余额
                    addBalance(account, acctTrans.getAmount());
                    // 释放未达金额
                    releaseUnreachAmount(account, acctTrans.getAmount());
                } else {
                    // 扣除余额
                    subtractBalance(account, acctTrans.getAmount());
                    // 释放冻结金额
                    releaseFreezeAmount(account, acctTrans.getAmount());
                }

                // 更新事务业务状态
                updateTransactionStatus(acctTrans, StatusEnum.DONE);
            }

            @Override
            public void checkParameter() {
                // TODO Auto-generated method stub

            }
        });

        return result.isSuccess();
    }

    protected boolean rollback(BusinessActionContext businessActionContext, boolean isCredit) {

        AccountTransResult result = BizTemplate.executeWithTransaction(accountTransactionTemplate, new BizCallback() {

            @Override
            public void execute(TransactionStatus status) {
                // 根据txid获取当前交易信息
                AccountTransaction acctTrans = getAccountTransaction(businessActionContext);
                // 首先检查分支事务是否存在，如果不存在，则直接返回真，不再处理，视为空回滚
                if (acctTrans == null) {
                    return;
                }

                // 幂等性验证
                checkIdempotent(acctTrans, StatusEnum.FAIL);

                Account account = getAccountAndCheck(acctTrans.getAccountNo());
                if (isCredit) {
                    // 释放未达金额
                    releaseUnreachAmount(account, acctTrans.getAmount());
                } else {
                    // 释放冻结金额
                    releaseFreezeAmount(account, acctTrans.getAmount());
                }

                // 更新事务业务状态
                updateTransactionStatus(acctTrans, StatusEnum.FAIL);
            }

            @Override
            public void checkParameter() {
                // TODO Auto-generated method stub

            }
        });

        return result.isSuccess();
    }

    protected void connectTxWithBusiness(BusinessActionContext businessActionContext,
            AccountTransRequest accountTransRequest, String shardingKey, boolean isCredit) {

        String txid = businessActionContext.getTxId() + businessActionContext.getActionId();
        LOGGER.info("{}: connect tx [{}] with account [{}]", isCredit ? "CREDIT" : "DEBIT", txid,
                isCredit ? accountTransRequest.getPeerBacc() : accountTransRequest.getBacc());
        addAccountTransaction(accountTransRequest, txid, shardingKey, isCredit);
    }

    protected AccountTransaction getAccountTransaction(BusinessActionContext businessActionContext) {
        String txid = businessActionContext.getTxId() + businessActionContext.getActionId();
        String shardingKey = (String) businessActionContext.getActionContext().get(XTSConstant.SHARDING_KEY);
        LOGGER.info("get account transaction by tx: [{}] and shardingkey: [{}]", txid, shardingKey);
        AccountTransaction acctTrans = accountTransactionDAO.findTransaction(txid, shardingKey);

        return acctTrans;
    }

    protected void updateTransactionStatus(AccountTransaction acctTrans, StatusEnum status) {
        LOGGER.info("update account transaction [{}] status [{}]", acctTrans.getTxId(), status.getCode());
        acctTrans.setStatus(status.getCode());
        accountTransactionDAO.updateTransactionStatus(acctTrans);
    }

    protected Account getAccountAndCheck(String targetAccount) {
        Account account = accountDAO.getAccountForUpdate(targetAccount);

        // 检查账户
        if (account == null) {
            throw new AcctCenterException(CodeEnum.ACCOUNT_NULL);
        }
        return account;
    }

    protected void releaseUnreachAmount(Account account, BigDecimal amount) {
        updateUnreach(account, amount, false);
    }

    protected void increaseUnreachAmount(Account account, BigDecimal amount) {
        updateUnreach(account, amount, true);
    }

    protected void increaseFreezeAmount(Account account, BigDecimal amount) {
        updateFreeze(account, amount, true);
    }

    protected void releaseFreezeAmount(Account account, BigDecimal amount) {
        updateFreeze(account, amount, false);
    }

    protected void checkBalance(Account account) {
        BigDecimal availableAmount = account.getBalance().subtract(account.getFreezeAmount());
        if (availableAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new AcctCenterException(CodeEnum.ACCOUNT_BALANCE_NOT_ENOUGH);
        }
    }

    protected void addBalance(Account account, BigDecimal amount) {
        updateBalance(account, amount, true);
    }

    protected void subtractBalance(Account account, BigDecimal amount) {
        updateBalance(account, amount, false);
    }

    protected void checkIdempotent(AccountTransaction acctTrans, StatusEnum expectedStatus) {
        if (acctTrans.getStatus() == StatusEnum.FAIL.getCode()) {
            throw new AcctCenterException(CodeEnum.IDEMPOTENT_EXCEPTION);
        }
    }

    private void addAccountTransaction(AccountTransRequest accountTransRequest, String txId, String shardingKey,
            boolean credit) {
        AccountTransaction acctTrans = new AccountTransaction();

        acctTrans.setTxId(txId);
        if (credit) {
            acctTrans.setAccountNo(accountTransRequest.getPeerBacc());
            acctTrans.setOperation(OperationEnum.CREDIT.getCode());
        } else {
            acctTrans.setAccountNo(accountTransRequest.getBacc());
            acctTrans.setOperation(OperationEnum.DEBIT.getCode());
        }
        acctTrans.setAmount(accountTransRequest.getTxnAmt());
        acctTrans.setStatus(StatusEnum.INIT.getCode());
        acctTrans.setShardingKey(shardingKey);

        accountTransactionDAO.addTransaction(acctTrans);
    }

    private void updateUnreach(Account account, BigDecimal amount, boolean add) {
        BigDecimal newUnreachAmount = add ? account.getUnreachAmount().add(amount)
                : account.getUnreachAmount().subtract(amount);
        account.setUnreachAmount(newUnreachAmount);
        accountDAO.updateUnreachAmount(account);
    }

    private void updateFreeze(Account account, BigDecimal amount, boolean add) {
        BigDecimal newFreezeAmount = add ? account.getFreezeAmount().add(amount)
                : account.getFreezeAmount().subtract(amount);
        account.setFreezeAmount(newFreezeAmount);
        accountDAO.updateFreezeAmount(account);
    }

    private void updateBalance(Account account, BigDecimal amount, boolean add) {
        BigDecimal newBalance = add ? account.getBalance().add(amount) : account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountDAO.updateBalance(account);
    }
}