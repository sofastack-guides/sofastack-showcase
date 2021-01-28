package com.aliyun.gts.financial.showcases.sofa.facade.tcc;

import com.alipay.dtx.client.core.api.domain.BusinessActionContext;
import com.alipay.dtx.client.core.spi.BusinessActionContextParameter;
import com.alipay.dtx.client.core.spi.ShardingKey;
import com.alipay.dtx.client.core.spi.TwoPhaseBusinessAction;
import com.aliyun.gts.financial.showcases.sofa.facade.request.AccountTransRequest;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountTransResult;

// 注解InjvmRemoting用于当发起方与参与方是同一个应用，也就是说参与方不需要被其他应用通过注册中心寻址来进行RPC调用
//@InjvmRemoting
public interface AcctDrawTccService {

        /**
         * <p>
         * 借记记账接口 TCC模式
         * </p>
         *
         * <p>
         * 根据账户的余额方向来对账户的资金进行流入 or 流出操作
         * </p>
         *
         * @param accountTransRequest   请求对象参数,详情请见{@link AccountTransRequest}
         * @param businessActionContext dtx框架中请求的上下文信息，框架级别参数，调用者不需传递，由dtx自动注入。
         * @return AccountTransResult 交易处理结果
         */
        @TwoPhaseBusinessAction(name = "debitAction", commitMethod = "commit", rollbackMethod = "rollback", antiSuspend = true)
        public AccountTransResult debit(String uid, AccountTransRequest accountTransRequest, @ShardingKey String shardingKey,
                        BusinessActionContext businessActionContext);

        /**
         * 二阶段提交
         * 
         * @param businessActionContext xts上下文
         * @return TwoPhaseResult#isSuccess() 是否成功，true-成功，false-失败
         */
        public boolean commit(BusinessActionContext businessActionContext);

        /**
         * 二阶段回滚
         * 
         * @param businessActionContext xts上下文
         * @return TwoPhaseResult#isSuccess() 是否成功，true-成功，false-失败
         */
        public boolean rollback(BusinessActionContext businessActionContext);

}