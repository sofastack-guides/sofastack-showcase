package com.aliyun.gts.financial.showcases.sofa.test.base;

import com.aliyun.gts.financial.showcases.sofa.SOFABootSpringApplication;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * reference: http://docs.spring.io/spring-boot/docs/1.4.2.RELEASE/reference/htmlsingle/#boot-features-testing
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SOFABootSpringApplication.class)
public abstract class AbstractTestBase {

    public static final Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    @Autowired
    protected ApplicationContext applicationContext;

    @Before
    public void setUp(){
        childSetUp();
    }

    /**
     * 子测试类每个测试方法执行前需要实现初始化方法
     */
    protected abstract void childSetUp();

}
