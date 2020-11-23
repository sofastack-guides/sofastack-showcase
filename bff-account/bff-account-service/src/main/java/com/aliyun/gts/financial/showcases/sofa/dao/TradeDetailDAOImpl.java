package com.aliyun.gts.financial.showcases.sofa.dao;

import com.aliyun.gts.financial.showcases.sofa.facade.model.TradeDetail;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.DataAccessException;

public class TradeDetailDAOImpl implements TradeDetailDAO {

	public SqlSessionTemplate sqlSession;  
	
	public void setSqlSession(SqlSessionTemplate sqlSession) {  
        this.sqlSession = sqlSession;  
	}

    @Override
    public int addTradeDetail(TradeDetail tradeDetail) throws DataAccessException {
        return sqlSession.insert("addTradeDetail", tradeDetail);
    }

    @Override
    public int updateTradeDetail(TradeDetail tradeDetail) throws DataAccessException {
    	return sqlSession.update("updateTradeDetail", tradeDetail);
    }

    @Override
    public TradeDetail getTradeDetail(String streamId) throws DataAccessException {
        return (TradeDetail) sqlSession.selectOne("getTradeDetail", streamId);
    }

    @Override
    public void deleteAllTradeDetail() throws DataAccessException {
        sqlSession.update("deleteAllTradeDetail");
    }

    @Override
    public int getSequenceId(String shardingCol) throws DataAccessException {
        return sqlSession.selectOne("getSequenceId", shardingCol);
    }

}