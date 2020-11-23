package com.aliyun.gts.financial.showcases.sofa.scheduler;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

import com.alipay.antschedulerclient.common.SplitChunkDataResult;
import com.alipay.antschedulerclient.handler.IClusterJobSplitHandler;
import com.alipay.antschedulerclient.model.ClusterJobSplitContext;
import com.alipay.antschedulerclient.model.chunk.ShardingChunkData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// 任务调度演示：第一层拆分
@Component
public class BatchSettleFirstSpliter implements IClusterJobSplitHandler<ShardingChunkData> {
    private final static Logger LOGGER = LoggerFactory.getLogger(BatchSettleFirstSpliter.class);
    /** 分片数量 */
    private static final int SHARD_COUNT = 10;

    @Override
    public String getName() {
        return "RATES_CALCULATE_SHARD_SPLITER";
    }

    @Override
    public ThreadPoolExecutor getThreadPool() {
        return null;
    }

    @Override
    public SplitChunkDataResult<ShardingChunkData> handle(ClusterJobSplitContext context) {
        SplitChunkDataResult<ShardingChunkData> splitChunkDataResult = new SplitChunkDataResult<>();
        ArrayList<ShardingChunkData> rangeChunkDatas = new ArrayList<>();

        for (int i = 0; i < SHARD_COUNT; i++) {
            rangeChunkDatas.add(new ShardingChunkData(String.valueOf(i)));
        }
        splitChunkDataResult.setChunkDatum(rangeChunkDatas);
        splitChunkDataResult.setSuccess(true);
        return splitChunkDataResult;
    }
    
}