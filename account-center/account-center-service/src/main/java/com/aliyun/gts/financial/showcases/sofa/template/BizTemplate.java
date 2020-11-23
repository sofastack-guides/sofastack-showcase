package com.aliyun.gts.financial.showcases.sofa.template;

import com.alipay.common.tracer.util.TracerContextUtil;
import com.aliyun.gts.financial.showcases.sofa.enums.CodeEnum;
import com.aliyun.gts.financial.showcases.sofa.exception.AcctCenterException;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountTransResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class BizTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(BizTemplate.class);

    /**
     * 处理模板
     *
     * @param callback 回调接口
     * @return T 处理结果
     */
    public static AccountTransResult executeWithTransaction(TransactionTemplate transactionTemplate,
            BizCallback callback) {

        AccountTransResult accountTransResult = new AccountTransResult();
        // Tracer演示：工具类TracerContextUtil
        String traceId = TracerContextUtil.getTraceId();
        accountTransResult.setTransLogId(traceId);
        try {

            // 1. 基本参数校验
            callback.checkParameter();

            transactionTemplate.execute((new TransactionCallbackWithoutResult() {

                @Override
                public void doInTransactionWithoutResult(TransactionStatus status) {
                    // 2. 回调处理结果
                    callback.execute(status);
                }
            }));

            accountTransResult.setSuccess(true);
            accountTransResult.setMsgCode(CodeEnum.SUCCESS.getCode());
            accountTransResult.setMsgText(CodeEnum.SUCCESS.getDesc());

        } catch (AcctCenterException ae) {
            accountTransResult.setSuccess(false);
            accountTransResult.setMsgCode(ae.getErrorCode().getCode());
            accountTransResult.setMsgText(ae.getErrorCode().getDesc());
            accountTransResult.setErrorMessage(ae.getMessage());

            if (ae.getErrorCode() == CodeEnum.TRANS_ACCOUNT_BUZY) {
                // LogUtil.info(LOCKER_LOGGER, "一阶段 - 存在账号繁忙, 账号为 :", ate.getErrorDesc());
                LOGGER.warn("一阶段 - 存在账号繁忙, 账号为 : {}", ae.getErrorDesc());
            } else if (ae.getErrorCode() == CodeEnum.IDEMPOTENT_EXCEPTION) {
                LOGGER.warn("幂等检查失败");
                accountTransResult.setSuccess(true);
                accountTransResult.setErrorMessage("");
            } else {
                // LogUtil.error(LOGGER, ate);
            }
        } catch (DataAccessException e) {
            // LogUtil.error(LOGGER, e);
            accountTransResult.setSuccess(false);
            accountTransResult.setMsgCode(CodeEnum.DB_EXCEPTION.getCode());
            accountTransResult.setMsgText(CodeEnum.DB_EXCEPTION.getDesc());
            accountTransResult.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            // LogUtil.error(LOGGER, e);
            accountTransResult.setSuccess(false);
            accountTransResult.setMsgCode(CodeEnum.SYSTEM_EXCEPTION.getCode());
            accountTransResult.setMsgText(CodeEnum.SYSTEM_EXCEPTION.getDesc());
            accountTransResult.setErrorMessage(e.getMessage());
        }

        LOGGER.info("success: {}; {}: {}", accountTransResult.isSuccess(), accountTransResult.getMsgCode(),
                accountTransResult.getErrorMessage());

        return accountTransResult;
    }
}