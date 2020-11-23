package com.aliyun.gts.financial.showcases.sofa.mq;

import com.aliyun.gts.financial.showcases.sofa.dao.AccountPointDAO;
import com.aliyun.gts.financial.showcases.sofa.dao.PointRecordDAO;
import com.aliyun.gts.financial.showcases.sofa.dynamic.PointConfig;
import com.aliyun.gts.financial.showcases.sofa.facade.model.AccountPoint;
import com.aliyun.gts.financial.showcases.sofa.facade.model.PointRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import io.openmessaging.api.Action;
import io.openmessaging.api.ConsumeContext;
import io.openmessaging.api.Message;
import io.openmessaging.api.MessageListener;

@Component
public class MessageListenerImpl implements MessageListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageListenerImpl.class);

    @Autowired
    private AccountPointDAO accountPointDAO;

    @Autowired
    private PointRecordDAO pointRecordDAO;

    @Autowired
    PointConfig pointConfig;

    @Autowired
    TransactionTemplate apTransactionTemplate;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        // 动态配置：2. 获取动态配置参数的实时值
        double pointToAdd = pointConfig.getPointValue();
        
        String streamId = message.getKey();
        String accountNo = new String(message.getBody());
        LOGGER.info("add [{}] point for account [{}]", pointToAdd, accountNo);

        // 幂等性判断
        if (!checkIdempotent(message)) {
            return Action.ReconsumeLater;
        }

        try {

            apTransactionTemplate.execute(new TransactionCallbackWithoutResult() {

                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {

                    // 插入或更新积分
                    AccountPoint accountPoint = accountPointDAO.findAccountPoint(accountNo);
                    if (accountPoint == null) {
                        AccountPoint newAccountPoint = new AccountPoint();
                        newAccountPoint.setPoint(pointToAdd);
                        newAccountPoint.setAccountNo(accountNo);
                        newAccountPoint.setStatus(1);
                        accountPointDAO.addAccountPoint(newAccountPoint);
                    } else {
                        double newPoint = accountPoint.getPoint() + pointToAdd;
                        accountPoint.setPoint(newPoint);
                        accountPoint.setStatus(1);
                        accountPointDAO.updateAccountPoint(accountPoint);
                    }

                    // 记录积分更新
                    PointRecord pointRecord = new PointRecord();
                    pointRecord.setStreamId(streamId);
                    pointRecord.setAccountNo(accountNo);
                    pointRecordDAO.addPointRecord(pointRecord);
                }

            });

            LOGGER.info("add point success");
            return Action.CommitMessage;
        } catch (Exception e) {
            LOGGER.error("add point failed: {}", e.getMessage(), e);
            return Action.ReconsumeLater;
        }
    }

    private boolean checkIdempotent(Message message) {
        try {
            String streamId = message.getKey();
            PointRecord pointRecord = pointRecordDAO.findPointRecord(streamId);
            if (pointRecord == null) {
                LOGGER.info("check idempotent success");
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("check idempotent failed unexpected: {}", e.getMessage(), e);
            return false;
        }
        LOGGER.error("check idempotent fail because found existing record");
        return false;
    }

}