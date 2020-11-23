package com.aliyun.gts.financial.showcases.sofa.dao.impl;

import java.util.HashMap;
import java.util.Map;

import com.aliyun.gts.financial.showcases.sofa.dao.AccountTransactionDAO;
import com.aliyun.gts.financial.showcases.sofa.facade.model.AccountTransaction;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.DataAccessException;

public class AccountTransactionDAOImpl implements AccountTransactionDAO {

    public SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public void addTransaction(AccountTransaction accountTransaction) throws DataAccessException {
        sqlSession.insert("addAccountTransaction", accountTransaction);
    }

    @Override
    public AccountTransaction findTransaction(String txId, String shardingKey) throws DataAccessException {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("txId", txId);
        param.put("shardingKey", shardingKey);
        return (AccountTransaction) sqlSession.selectOne("getAccountTransaction", param);
    }

    @Override
    public int updateTransactionStatus(AccountTransaction accountTransaction) {
        return sqlSession.update("updateAccountTransactionStatus", accountTransaction);
    }

    @Override
    public void deleteTransaction(String txId, String shardingKey) throws DataAccessException {
        sqlSession.delete("deleteAccountTransaction", txId);
    }

    @Override
    public void deleteAllTransaction() throws DataAccessException {
        sqlSession.delete("deleteAllTransaction");
    }


}