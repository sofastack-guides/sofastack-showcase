package com.aliyun.gts.financial.showcases.sofa.dao.impl;

import com.aliyun.gts.financial.showcases.sofa.dao.AccountDAO;
import com.aliyun.gts.financial.showcases.sofa.facade.model.Account;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.DataAccessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountDAOImpl implements AccountDAO {

    public SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public void addAccount(Account account) throws DataAccessException {
        sqlSession.insert("addAccount", account);
    }

    @Override
    public int updateBalance(Account account) throws DataAccessException {
        return sqlSession.update("updateBalance", account);
    }

    @Override
    public Account getAccount(String accountNo) throws DataAccessException {
        return (Account) sqlSession.selectOne("getAccount", accountNo);
    }

    @Override
    public Account getAccountForUpdate(String accountNo) throws DataAccessException {
        return (Account) sqlSession.selectOne("getAccountForUpdate", accountNo);
    }

    @Override
    public int updateFreezeAmount(Account account) throws DataAccessException {
        return sqlSession.update("updateFreezeAmount", account);
    }

    @Override
    public int updateUnreachAmount(Account account) throws DataAccessException {
        return sqlSession.update("updateUnreachAmount", account);
    }

    @Override
    public void deleteAllAccount() throws DataAccessException {
        sqlSession.update("deleteAccount");
    }

    @Override
    public List<Account> getAccountByRange(String shard, int start, int end) throws DataAccessException {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("shard", shard);
        param.put("start", start);
        param.put("end", end);

        List<Account> accounts = sqlSession.selectList("getAccountByRange", param);
        return accounts;
    }

    @Override
    public int getMaxId(String shard) throws DataAccessException {
        int maxId = (int) sqlSession.selectOne("queryMaxId", shard);
        return maxId;
    }

    @Override
    public void batchInsertAccounts(List<Account> accounts) {
        sqlSession.insert("batchInsertAccounts", accounts);
    }
}