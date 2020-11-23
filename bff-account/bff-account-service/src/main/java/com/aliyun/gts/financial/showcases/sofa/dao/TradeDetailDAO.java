package com.aliyun.gts.financial.showcases.sofa.dao;

import com.aliyun.gts.financial.showcases.sofa.facade.model.TradeDetail;

import org.springframework.dao.DataAccessException;

public interface TradeDetailDAO {

    int addTradeDetail(TradeDetail tradeDetail) throws DataAccessException;

    int updateTradeDetail(TradeDetail tradeDetail) throws DataAccessException;

    TradeDetail getTradeDetail(String streamId) throws DataAccessException;

    void deleteAllTradeDetail() throws DataAccessException;

    int getSequenceId(String shardingCol) throws DataAccessException;
    
}