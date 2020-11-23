package com.aliyun.gts.financial.showcases.sofa.dao;


import com.aliyun.gts.financial.showcases.sofa.facade.model.PointRecord;

import org.springframework.dao.DataAccessException;

public interface PointRecordDAO {
    void addPointRecord(PointRecord pointRecord) throws DataAccessException;

    PointRecord findPointRecord(String streamId) throws DataAccessException; 
}