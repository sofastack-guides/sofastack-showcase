package com.aliyun.gts.financial.showcases.sofa.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.openmessaging.api.Consumer;
import io.openmessaging.api.OMS;

@Configuration
public class NormalMqConsumer {
    @Autowired
    private MqConfig            mqConfig;

    @Autowired
    private MessageListenerImpl messageListener;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public Consumer buildConsumer() {
        Consumer consumer = OMS.builder().driver("sofamq").build(mqConfig.getMqProperties())
            .createConsumer(mqConfig.getMqProperties());
        consumer.subscribe(mqConfig.getTopic(), mqConfig.getTag(), messageListener);
        return consumer;
    }

}