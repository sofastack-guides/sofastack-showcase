package com.aliyun.gts.financial.showcases.sofa.extensions;

import java.util.List;

import com.alipay.sofa.rpc.api.ldc.LdcRouteJudgeResult;
import com.alipay.sofa.rpc.api.ldc.LdcRouteProvider;
import com.alipay.sofa.rpc.client.AddressHolder;
import com.alipay.sofa.rpc.client.ProviderGroup;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.core.request.SofaRequest;
import com.alipay.zoneclient.api.EnterpriseZoneClientHolder;
import com.aliyun.gts.financial.showcases.sofa.utils.UIDUtil;
import com.alipay.sofa.rpc.common.utils.CommonUtils;

// 单元化：实现自定义路由规则，根据目标方法的第一个参数的第一位补充0
public class CustomLdcRouteProvider implements LdcRouteProvider {
    @Override
    public LdcRouteJudgeResult uidGenerator(ConsumerConfig consumerConfig, SofaRequest sofaRequest) {
        LdcRouteJudgeResult result = new LdcRouteJudgeResult();

        //如果不是单元化模式，那么就直接返回false
        if (!EnterpriseZoneClientHolder.isZoneMode()) {
            return result;
        }
        
        // 判断是否包含GZONE服务，如果是则返回-1
        // 获取 AddressHolder
        AddressHolder addressHolder = consumerConfig.getConsumerBootstrap().getCluster().getAddressHolder();
        // 获取 各个 ProviderGroup
        List<ProviderGroup> providerGroups = addressHolder.getProviderGroups();
        for (ProviderGroup providerGroup : providerGroups) {
            String name = providerGroup.getName();
            // 判断 GZ 有服务
            if(name.contains("GZ")&& !providerGroup.isEmpty()){
                result.setSuccess(true);
                result.setRouteId("-1");
                return result;
            }
        }

        Object[] methodArgs = sofaRequest.getMethodArgs();
        if (CommonUtils.isEmpty(methodArgs)) {
            result.setSuccess(false);
            return result;
        }
        Object firstMethodArg = methodArgs[0];
        if (firstMethodArg instanceof String) {
            String accountNo = (String) firstMethodArg;
            String uid = UIDUtil.extractUidFromAccountNo(accountNo);
            result.setSuccess(true);
            result.setRouteId(uid);
            return result;
        }
        result.setSuccess(false);
        return result;
    }

    @Override
    public int order() {
        return 10;
    }
}