package com.aliyun.gts.financial.showcases.sofa;

import com.alipay.sofa.rpc.ldc.LdcProviderManager;
import com.aliyun.gts.financial.showcases.sofa.extensions.CustomLdcRouteProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

@ImportResource({"classpath:META-INF/bff-account/*.xml"})
@org.springframework.boot.autoconfigure.SpringBootApplication
public class SOFABootSpringApplication {
    private static final Logger logger = LoggerFactory.getLogger(SOFABootSpringApplication.class);

    public static void main(String[] args){

        // 单元化：添加自定义路由规则
        LdcProviderManager.getInstance().registeLdcRouteProvider(new CustomLdcRouteProvider());

        SpringApplication springApplication = new SpringApplication(SOFABootSpringApplication.class);
        ApplicationContext applicationContext = springApplication.run(args);
       
        if (logger.isInfoEnabled()){
            logger.info("Current Application Context : " + applicationContext);
        }
    }
}
