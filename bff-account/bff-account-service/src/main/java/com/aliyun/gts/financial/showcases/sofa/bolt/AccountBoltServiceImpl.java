package com.aliyun.gts.financial.showcases.sofa.bolt;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.sofamq.client.TransactionHelper;
import com.alipay.sofa.sofamq.client.UserPropKey;
import com.aliyun.gts.financial.showcases.sofa.dao.TradeDetailDAO;
import com.aliyun.gts.financial.showcases.sofa.facade.bolt.AccountBoltService;
import com.aliyun.gts.financial.showcases.sofa.facade.enums.StatusEnum;
import com.aliyun.gts.financial.showcases.sofa.facade.inner.AccountTransferService;
import com.aliyun.gts.financial.showcases.sofa.facade.model.TradeDetail;
import com.aliyun.gts.financial.showcases.sofa.facade.request.AccountTransRequest;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountTransResult;
import com.aliyun.gts.financial.showcases.sofa.mq.MqConfig;
import com.aliyun.gts.financial.showcases.sofa.utils.UIDUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.TransactionStatus;

import io.openmessaging.api.Message;
import io.openmessaging.api.SendResult;
import io.openmessaging.api.TransactionalResult;
import io.openmessaging.api.transaction.LocalTransactionExecuter;
import io.openmessaging.api.transaction.TransactionProducer;

@SofaService(bindings = { @SofaServiceBinding(bindingType = "bolt") })
public class AccountBoltServiceImpl implements AccountBoltService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AccountBoltServiceImpl.class);

    @Autowired
    AccountTransferService accountTransferService;

    @Autowired
    private TradeDetailDAO tradeDetailDAO;

    @Autowired
    private TransactionProducer transactionProducer;

    @Autowired
    private MqConfig mqConfig;

    @Autowired
    private TransactionTemplate bffTransactionTemplate;

    // DTX：嵌套TCC
    @Override
    public AccountTransResult transerTrade(AccountTransRequest accountTransRequest) {

        LOGGER.info("transer trade starts ......");
        AccountTransResult accountTransResult = null;

        try {
            // 结合分片号和分布式序列号生成流水号
            String streamId = generateStreamId(accountTransRequest);

            // 记录流水
            LOGGER.info("trade detail with stream id: {}", streamId);
            initTrade(accountTransRequest, streamId);

            // 转账积分分布式事务
            accountTransResult = accTransDtxTradeWithPoint(accountTransRequest, streamId);

            // 更新流水状态
            if (accountTransResult.isSuccess()) {
                finishTrade(streamId);
            } else {
                failTrade(streamId);
            }

            accountTransResult.setTransLogId(streamId + " | " + accountTransResult.getTransLogId());

        } catch (Exception e) {
            return new AccountTransResult(false, "TRANSFER_TRADE_FAIL_UNEXPEECTED", e.getMessage());
        }

        return accountTransResult;
    }

    private AccountTransResult accTransDtxTradeWithPoint(AccountTransRequest accountTransRequest, String streamId) {

        return bffTransactionTemplate.execute(new TransactionCallback<AccountTransResult>() {
            @Override
            public AccountTransResult doInTransaction(TransactionStatus status) {
                try {
                    
                    // 1. 发送积分事务消息，但不触发投递
                    Message message = new Message(mqConfig.getTopic(), mqConfig.getTag(),
                            accountTransRequest.getBacc().getBytes());
                    // message.setSystemProperties("systemProperties");
                    message.setKey(streamId);
                    // 单元化: 根据uid发送消息给具体的RZone
                    String fromUid = UIDUtil.extractUidFromAccountNo(accountTransRequest.getBacc());
                    message.putUserProperties(UserPropKey.CELL_UID, fromUid);

                    TransactionalResult msgPubResult = transactionProducer.prepare(message);
                    // 将事务型消息与本地事务绑定：
                    // 当前本地事务成功，则自动触发 msgPubResult.commit();
                    // 当前本地事务失败，则自动触发 msgPubResult.rollback();
                    TransactionHelper.synchronizeSpringTransaction(msgPubResult);

                    // 2. 转账，TCC
                    return accountTransferService.transerByTcc(accountTransRequest, streamId);
                } catch (Exception e) {
                    status.setRollbackOnly();
                    return new AccountTransResult(false, "nested tcc failed", e.getMessage());
                }
            }
        });

    }

    private AccountTransResult accTransDtxTradeWithPoint2(AccountTransRequest accountTransRequest, String streamId) {
        // 1. 发送积分事务消息，但不触发投递
        Message message = new Message(mqConfig.getTopic(), mqConfig.getTag(), accountTransRequest.getBacc().getBytes());
        // message.setSystemProperties("systemProperties");
        message.setKey(streamId);
        // 单元化: 根据uid发送消息给具体的RZone
        // message.putUserProperties(UserPropKey.CELL_UID, "01");

        SendResult sendResult = transactionProducer.send(message, new LocalTransactionExecuter() {
            @Override
            public io.openmessaging.api.transaction.TransactionStatus execute(Message message, Object arg) {
                try {
                    // 2. 转账，嵌套TCC
                    AccountTransResult result = accountTransferService.transerByTcc(accountTransRequest, streamId);
                    return result.isSuccess() ? io.openmessaging.api.transaction.TransactionStatus.CommitTransaction
                            : io.openmessaging.api.transaction.TransactionStatus.RollbackTransaction;
                } catch (Exception e) {
                    return io.openmessaging.api.transaction.TransactionStatus.RollbackTransaction;
                }
            }
        }, null);

        if (sendResult != null) {
            return new AccountTransResult(true, "", "");
        } else {
            return new AccountTransResult(false, "", "");
        }
    }

    private String generateStreamId(AccountTransRequest accountTransRequest) {
        int sequenceId = tradeDetailDAO.getSequenceId(accountTransRequest.getBacc());
        String uid = UIDUtil.extractUidFromAccountNo(accountTransRequest.getBacc());
        String streamId = uid + sequenceId;
        return streamId;
    }

    private void initTrade(AccountTransRequest accountTransRequest, String streamId) {
        TradeDetail tradeDetail = new TradeDetail();

        tradeDetail.setStreamId(streamId);
        tradeDetail.setCustacIn(accountTransRequest.getPeerBacc());
        tradeDetail.setCustacOut(accountTransRequest.getBacc());
        tradeDetail.setAmount(accountTransRequest.getTxnAmt());
        tradeDetail.setTradeStatus(StatusEnum.INIT.getCode());
        tradeDetail.setTradeDate(accountTransRequest.getTxnTime());

        tradeDetailDAO.addTradeDetail(tradeDetail);
    }

    private void finishTrade(String streamId) {
        int updateNum = this.updateTradeStatus(streamId, StatusEnum.DONE);
        if (updateNum == 0) {
            throw new RuntimeException(String.format("set deal success exception,streamId:%s", streamId));
        }
    }

    private void failTrade(String streamId) {
        int updateNum = this.updateTradeStatus(streamId, StatusEnum.FAIL);
        if (updateNum == 0) {
            throw new RuntimeException(String.format("set deal failed exception,streamId:%s", streamId));
        }
    }

    private int updateTradeStatus(String streamId, StatusEnum dealStatusEnum) {
        TradeDetail tradeDetail = new TradeDetail();
        tradeDetail.setStreamId(streamId);
        tradeDetail.setTradeStatus(dealStatusEnum.getCode());
        return tradeDetailDAO.updateTradeDetail(tradeDetail);
    }

}