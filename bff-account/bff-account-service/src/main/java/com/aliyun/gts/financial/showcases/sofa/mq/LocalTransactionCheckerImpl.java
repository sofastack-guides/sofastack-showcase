package com.aliyun.gts.financial.showcases.sofa.mq;

import com.aliyun.gts.financial.showcases.sofa.dao.TradeDetailDAO;
import com.aliyun.gts.financial.showcases.sofa.facade.enums.StatusEnum;
import com.aliyun.gts.financial.showcases.sofa.facade.model.TradeDetail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.openmessaging.api.Message;
import io.openmessaging.api.transaction.LocalTransactionChecker;
import io.openmessaging.api.transaction.TransactionStatus;

@Component
public class LocalTransactionCheckerImpl implements LocalTransactionChecker {
    @Autowired
    private TradeDetailDAO tradeDetailDAO;

    @Override
    public TransactionStatus check(Message msg) {

        TradeDetail tradeDetail = tradeDetailDAO.getTradeDetail(msg.getKey());
        if (tradeDetail == null) {
            return TransactionStatus.RollbackTransaction;
        }
        if (tradeDetail.getTradeStatus() == StatusEnum.DONE.getCode()) {
            return TransactionStatus.CommitTransaction;
        } else {
            return TransactionStatus.RollbackTransaction;
        }
    }

}