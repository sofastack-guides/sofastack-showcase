package com.aliyun.gts.financial.showcases.sofa.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadPoolExecutor;

import com.alipay.antschedulerclient.common.SplitChunkDataResult;
import com.alipay.antschedulerclient.handler.IClusterJobSplitHandler;
import com.alipay.antschedulerclient.model.ClusterJobSplitContext;
import com.alipay.antschedulerclient.model.chunk.IChunkData;
import com.alipay.antschedulerclient.model.chunk.RangeChunkData;
import com.alipay.sofa.dbp.RouteCondition;
import com.alipay.sofa.dbp.RouteParameters;
import com.aliyun.gts.financial.showcases.sofa.dao.AccountDAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BatchSettleSecondSpliter implements IClusterJobSplitHandler<RangeChunkData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchSettleSecondSpliter.class);

    private static final int RANGE_SIZE = 5;

    @Autowired
    private AccountDAO accountDAO;

    @Override
    public String getName() {
        return "RATES_CALCULATE_SECOND_SPLITER";
    }

    @Override
    public ThreadPoolExecutor getThreadPool() {
        return null;
    }

    @Override
    public SplitChunkDataResult<RangeChunkData> handle(ClusterJobSplitContext context) {
        SplitChunkDataResult<RangeChunkData> splitChunkDataResult = new SplitChunkDataResult<>();
        IChunkData chunkData = context.getChunkData();
        String shard = chunkData.getShardingRule();

        int shardSize = getShardSize(shard);

        // 从任务调度界面上获取自定义参数值
        Integer rangeSize = (Integer) context.getCustomParam("rangeSize");
        if (null == rangeSize) {
            rangeSize = RANGE_SIZE;
        }
        LOGGER.info("count per range is: {}", rangeSize);

        int rangeCount = (int) Math.ceil((double) shardSize / rangeSize);
        LOGGER.info("current shard is {} and splits into {} ranges", shard, rangeCount);
        ArrayList<RangeChunkData> rangeChunkDatas = new ArrayList<>();
        for (int range = 1; range <= rangeCount; range++) {
            int start = (range - 1) * rangeSize + 1;
            int end = range * rangeSize;
            rangeChunkDatas.add(new RangeChunkData(shard, String.valueOf(start), String.valueOf(end)));
        }

        splitChunkDataResult.setChunkDatum(rangeChunkDatas);
        splitChunkDataResult.setSuccess(true);
        return splitChunkDataResult;
    }

    private int getShardSize(String shard) {
        // DBP演示：hint语句由拦截器添加
        // 以下语句会被拦截器拦截生成DBP hint加在sql 语句前
        // /*+DBP: $ROUTE={GROUP_ID(分库位),TABLE_NAME(物理表名)}*/ SQL 语句
        RouteParameters routeParameters = RouteCondition.newRouteParameters();
        routeParameters.setGroupId(shard);
        routeParameters.setTargetTable("account_"+shard);
        int shardSize = accountDAO.getMaxId(shard);
        LOGGER.info("shard {} max id is {}", shard, shardSize);
        return shardSize;
    }

}