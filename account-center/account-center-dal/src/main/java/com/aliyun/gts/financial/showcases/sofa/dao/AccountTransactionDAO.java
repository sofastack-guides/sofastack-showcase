package com.aliyun.gts.financial.showcases.sofa.dao;

import com.aliyun.gts.financial.showcases.sofa.facade.model.AccountTransaction;

import org.springframework.dao.DataAccessException;

public interface AccountTransactionDAO {

    void addTransaction(AccountTransaction accountTransaction) throws DataAccessException;

    AccountTransaction findTransaction(String txId, String shardingKey) throws DataAccessException;

    int updateTransactionStatus(AccountTransaction accountTransaction) throws DataAccessException;

    void deleteTransaction(String txId, String shardingKey) throws DataAccessException;

    void deleteAllTransaction() throws DataAccessException;
}
