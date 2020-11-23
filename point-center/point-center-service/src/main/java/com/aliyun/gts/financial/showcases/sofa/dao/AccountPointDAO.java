package com.aliyun.gts.financial.showcases.sofa.dao;

import java.sql.SQLException;
import com.aliyun.gts.financial.showcases.sofa.facade.model.AccountPoint;

import org.springframework.dao.DataAccessException;


public interface AccountPointDAO {

    void addAccountPoint(AccountPoint accountPoint) throws DataAccessException;
    
    AccountPoint findAccountPoint(String account) throws DataAccessException;
    
    void updateAccountPoint(AccountPoint accountPoint) throws DataAccessException;
 
    void deleteAccountPoint(String account) throws DataAccessException;
    
    void deleteAllAccountPoint() throws DataAccessException;
}
