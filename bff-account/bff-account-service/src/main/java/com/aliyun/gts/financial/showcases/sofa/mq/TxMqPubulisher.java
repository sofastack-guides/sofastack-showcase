package com.aliyun.gts.financial.showcases.sofa.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.openmessaging.api.OMS;
import io.openmessaging.api.transaction.TransactionProducer;

@Configuration
public class TxMqPubulisher {
    @Autowired
    private MqConfig mqConfig;

    @Autowired
    private LocalTransactionCheckerImpl localTransactionCheckerImpl;

    @Bean(initMethod = "start", destroyMethod = "shutdown", name = "transactionProducer")
    public TransactionProducer buildTransactionProducer() {
        TransactionProducer txProducer = OMS.builder().driver("sofamq").build(mqConfig.getMqProperties())
                .createTransactionProducer(mqConfig.getMqProperties(), localTransactionCheckerImpl);
        return txProducer;
    }
}