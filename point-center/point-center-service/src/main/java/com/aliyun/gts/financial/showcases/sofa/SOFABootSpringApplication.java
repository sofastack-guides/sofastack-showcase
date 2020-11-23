package com.aliyun.gts.financial.showcases.sofa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

@ImportResource({"classpath:META-INF/point-center/*.xml"})
@org.springframework.boot.autoconfigure.SpringBootApplication
public class SOFABootSpringApplication {
    private static final Logger logger = LoggerFactory.getLogger(SOFABootSpringApplication.class);

    public static void main(String[] args){

        SpringApplication springApplication = new SpringApplication(SOFABootSpringApplication.class);
        ApplicationContext applicationContext = springApplication.run(args);

        if (logger.isInfoEnabled()){
            logger.info("Current Application Context : " + applicationContext);
        }
    }
}
