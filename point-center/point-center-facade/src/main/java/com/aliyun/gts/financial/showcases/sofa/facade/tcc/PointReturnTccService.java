package com.aliyun.gts.financial.showcases.sofa.facade.tcc;

import com.alipay.dtx.client.core.api.domain.BusinessActionContext;
import com.alipay.dtx.client.core.spi.BusinessActionContextParameter;
import com.alipay.dtx.client.core.spi.TwoPhaseBusinessAction;

public interface PointReturnTccService {

    // TCC二阶段传值：演示通过BusinessActionContext进行传递
    // 1. 给需要传递的参数添加注解 @BusinessActionContextParameter(paramName = "accountNo")
    //    *注意一定需要指定paramName，否则会导致存储参数的key为空
    @TwoPhaseBusinessAction(name = "addPointAction", commitMethod = "commit", rollbackMethod = "rollback")
    public boolean addPoint(BusinessActionContext businessActionContext,
            @BusinessActionContextParameter(paramName = "accountNo") String accountNo);

    public boolean commit(BusinessActionContext businessActionContext);

    public boolean rollback(BusinessActionContext businessActionContext);

}