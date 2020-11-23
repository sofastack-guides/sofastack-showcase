package com.aliyun.gts.financial.showcases.sofa.dao;

import com.aliyun.gts.financial.showcases.sofa.facade.model.PointRecord;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.DataAccessException;

public class PointRecordDAOImpl implements PointRecordDAO {
    public SqlSessionTemplate sqlSession;
	
	public void setSqlSession(SqlSessionTemplate sqlSession) {  
        this.sqlSession = sqlSession;  
	}

    @Override
    public void addPointRecord(PointRecord pointRecord) throws DataAccessException {
        sqlSession.insert("addPointRecord", pointRecord);
    }

    @Override
    public PointRecord findPointRecord(String streamId) throws DataAccessException {
        return (PointRecord) sqlSession.selectOne("getPointRecord", streamId);
    }

}