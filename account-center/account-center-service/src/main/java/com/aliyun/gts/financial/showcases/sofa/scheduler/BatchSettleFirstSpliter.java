package com.aliyun.gts.financial.showcases.sofa.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.alipay.antschedulerclient.common.SplitChunkDataResult;
import com.alipay.antschedulerclient.handler.IClusterJobSplitHandler;
import com.alipay.antschedulerclient.model.ClusterJobSplitContext;
import com.alipay.antschedulerclient.model.chunk.ShardingChunkData;
import com.alipay.routeclient.UidRange;
import com.alipay.routeclient.ZoneInfo;
import com.alipay.sofa.rpc.common.RouteUtil;
import com.alipay.sofa.rpc.common.RpcConfigs;
import com.alipay.sofa.rpc.context.RpcRuntimeContext;
import com.alipay.zoneclient.util.ZoneClientUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BatchSettleFirstSpliter implements IClusterJobSplitHandler<ShardingChunkData> {
    private final static Logger LOGGER = LoggerFactory.getLogger(BatchSettleFirstSpliter.class);

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

        // 单元化：获取当前实例所在的单元以及对应的uid范围
        String appName = (String) RpcConfigs.getStringValue(RpcRuntimeContext.APP_NAME);
        LOGGER.info("app name: {}", appName);
        ZoneClientUtil.initialize(appName, RouteUtil.getConfigZmgUrl());

        List<UidRange> curUidRanges = ZoneClientUtil.getCurrZoneUidRange();
        for (UidRange ur : curUidRanges) {
            LOGGER.info("my uid range is: from {} to {}", ur.getUidMinValue(), ur.getUidMaxValue());
            for (int i = ur.getUidMinValue(); i <= ur.getUidMaxValue(); i++) {
                String shardingRule = i < 10 ? ("0" + i) : String.valueOf(i);
                rangeChunkDatas.add(new ShardingChunkData(shardingRule));
            }
        }

        // List<ZoneInfo> allZoneInfo = ZoneClientUtil.queryAllZoneInfo();
        // for (ZoneInfo zoneInfo : allZoneInfo) {
        //     List<UidRange> uidRanges = zoneInfo.zoneGroupGet().findUidRange();
        //     for (UidRange ur : uidRanges) {
        //         LOGGER.info("2 -- zone: {}: uid {} - {}", zoneInfo.getZoneName(), ur.getUidMinValue(), ur.getUidMaxValue());
        //     }
        // }
        
        splitChunkDataResult.setChunkDatum(rangeChunkDatas);
        splitChunkDataResult.setSuccess(true);
        return splitChunkDataResult;
    }
    
}