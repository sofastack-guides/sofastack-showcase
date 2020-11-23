package com.aliyun.gts.financial.showcases.sofa.mq;

import java.util.Properties;

import com.alipay.sofa.sofamq.client.PropertyKeyConst;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    @Value("${com.antcloud.mw.access}")
    private String accessKey;
    @Value("${com.antcloud.mw.secret}")
    private String secretKey;
    @Value("${com.antcloud.antvip.endpoint}")
    private String endpoint;
    @Value("${com.alipay.instanceid}")
    private String instanceId;
    // 用于专有云
    @Value("${sofamq.data-center}")
    private String dataCenter;
    @Value("${sofamq.groupId}")
    private String groupId;

    @Value("${sofamq.topic}")
    private String topic;
    @Value("${sofamq.tag}")
    private String tag;

    public String getTopic() {
        return this.topic;
    }

    public String getTag() {
        return this.tag;
    }

    public Properties getMqProperties() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.ACCESS_KEY, this.accessKey);
        properties.setProperty(PropertyKeyConst.SECRET_KEY, this.secretKey);
        properties.setProperty(PropertyKeyConst.ENDPOINT, "acvip://" + this.endpoint);
        properties.setProperty(PropertyKeyConst.INSTANCE_ID, this.instanceId);
        //properties.setProperty(PropertyKeyConst.DATA_CENTER, this.dataCenter);
        properties.setProperty(PropertyKeyConst.GROUP_ID, this.groupId);
        return properties;
    }
}