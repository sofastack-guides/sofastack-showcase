package com.aliyun.gts.financial.showcases.sofa.dao;

import java.sql.SQLException;

import com.aliyun.gts.financial.showcases.sofa.facade.model.AccountPoint;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.DataAccessException;



public class AccountPointDAOImpl implements AccountPointDAO {
	
	public SqlSessionTemplate sqlSession;
	
	public void setSqlSession(SqlSessionTemplate sqlSession) {  
        this.sqlSession = sqlSession;  
	}

	@Override
	public void addAccountPoint(AccountPoint accountPoint) throws DataAccessException {
    	sqlSession.insert("addAccountPoint", accountPoint);
	}

	@Override
	public AccountPoint findAccountPoint(String accountNo) throws DataAccessException {
        return (AccountPoint) sqlSession.selectOne("getAccountPoint", accountNo);
	}

	@Override
	public void deleteAccountPoint(String accountNo) throws DataAccessException {
    	sqlSession.delete("deleteAccountPoint", accountNo);
	}
	
	
	@Override
	public void updateAccountPoint(AccountPoint accountPoint) throws DataAccessException{
		sqlSession.update("updateAccountPoint", accountPoint);
    }
	
	@Override
	public void  deleteAllAccountPoint() throws DataAccessException{
		sqlSession.update("deleteAllAccountPoint");
    }

}
