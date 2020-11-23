package com.aliyun.gts.financial.showcases.sofa.scheduler;

import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

import com.alipay.antschedulerclient.common.ClientCommonResult;
import com.alipay.antschedulerclient.handler.ISimpleJobHandler;
import com.alipay.antschedulerclient.model.JobExecuteContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CheckHandler implements ISimpleJobHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckHandler.class);

    @Override
    public String getName() {
        return "CheckJobHandler";
    }

    @Override
    public ThreadPoolExecutor getThreadPool() {
        return null;
    }

    @Override
    public ClientCommonResult handle(JobExecuteContext context) {
        LOGGER.info("[simple-task-syncUserHandler] callback job");

        LOGGER.info("traceId in context: {}", context.getTracerId());

        Random random = new Random();
        int sleepTime = random.nextInt(5000);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            return ClientCommonResult.buildFailResult("sleep error");
        }
        int num = random.nextInt(100);
        if (num > 50) {
            LOGGER.info("random no: {}, for settle: {}", num, true);
            context.putCustomParams("forSettle", true);
        } else {
            LOGGER.info("random no: {}, for settle: {}", num, false);
            context.putCustomParams("forSettle", false);
        }

        return ClientCommonResult.buildSuccessResult();
    } 
}
