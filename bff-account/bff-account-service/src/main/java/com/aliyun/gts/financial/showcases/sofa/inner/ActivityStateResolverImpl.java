package com.aliyun.gts.financial.showcases.sofa.inner;

import com.alipay.dtx.client.core.api.ActivityStateResolver;
import com.aliyun.gts.financial.showcases.sofa.dao.TradeDetailDAO;
import com.aliyun.gts.financial.showcases.sofa.facade.enums.StatusEnum;
import com.aliyun.gts.financial.showcases.sofa.facade.model.TradeDetail;

import org.springframework.beans.factory.annotation.Autowired;

public class ActivityStateResolverImpl implements ActivityStateResolver {

    @Autowired
    private TradeDetailDAO tradeDetailDAO;
    
    @Override
    public int isDone(String businessType, String businessId) {
        // 根据业务流水来判断
        if(txStateQuery(businessType,businessId)){
            return ActivityStateResolver.DONE;
        }else {
            return ActivityStateResolver.NOT_DONE;
        }
    }

    private boolean txStateQuery(String businessType, String businessId) {
        TradeDetail tradeDetail = tradeDetailDAO.getTradeDetail(businessId);
        if (tradeDetail.getTradeStatus() == StatusEnum.DONE.getCode()) {
            return true;
        } else {
            return false;
        }
    }
}
