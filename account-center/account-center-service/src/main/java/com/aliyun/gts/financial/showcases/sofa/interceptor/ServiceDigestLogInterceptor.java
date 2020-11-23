package com.aliyun.gts.financial.showcases.sofa.interceptor;

import java.lang.reflect.Method;

import com.alipay.sofa.common.utils.ArrayUtil;
import com.alipay.sofa.common.utils.StringUtil;
import com.aliyun.gts.financial.showcases.sofa.enums.CodeEnum;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AbstractAccountResult;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 业务层操作类摘要日志拦截器
 *
 * @author ans
 * @version $Id: ServiceDigestLogInterceptor.java, v 0.1 2018-5-4 16:04:37 Exp $
 */
public class ServiceDigestLogInterceptor implements MethodInterceptor {

    /**
     * 正常日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDigestLogInterceptor.class);

    private static final Logger SERVICE_DIGEST_LOGGER = LoggerFactory.getLogger("SERVICE-DIGEST");

    /**
     * 分隔符
     */
    public static final String SEP = ",";

    /**
     * 点分割符
     */
    public static final String DOT_SEP = ".";

    private static final char RIGHT_TAG = ']';

    /**
     * 默认值
     */
    public static final String DEFAULT_STR = "-";

    /**
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        // 被拦截的方法
        Method method = invocation.getMethod();

        // 被拦截方法签名："类名.方法名"
        final String invocationSignature = method.getDeclaringClass().getSimpleName() + DOT_SEP + method.getName();

        // 方法中的参数
        final Object[] arguments = invocation.getArguments();

        long startTime = System.currentTimeMillis();

        Object result = null;
        try {

            result = invocation.proceed();
            return result;

        } finally {

            try {

                final long elapseTime = System.currentTimeMillis() - startTime;

                final Object finalResult = result;

                SERVICE_DIGEST_LOGGER.info(buildDigestLog(invocationSignature, finalResult, elapseTime, arguments));

            } catch (Exception e) {
                LOGGER.error("service digest log failed: {}", e.getMessage(), e);
            }

        }
    }

    /**
     * 构造摘要日志
     *
     * @param invocationSignature 方法签名
     * @param result              返回结果
     * @param elapseTime          耗时
     * @param arguments           参数
     * @return 摘要日志
     */
    private String buildDigestLog(String invocationSignature, Object result, long elapseTime, Object[] arguments) {

        StringBuilder sb = new StringBuilder(200);
        sb.append("[");

        // 主体信息
        sb.append(buildResponseInfo(invocationSignature, result, elapseTime));

        // 参数信息
        sb.append(buildRequestInfo(arguments));

        sb.append("]");
        return sb.toString();
    }

    /**
     * 构造参数信息
     *
     * @param arguments 参数对象
     * @return 参数日志
     */
    private String buildRequestInfo(Object[] objs) {

        StringBuilder sb = new StringBuilder();

        try {

            if (ArrayUtil.isNotEmpty(objs)) {
                for (Object o : objs) {
                    if (o == null) {
                        sb.append(DEFAULT_STR).append(SEP);
                    } else {
                        sb.append(StringUtil.defaultIfBlank(o.toString(), DEFAULT_STR))
                                .append(SEP);
                    }
                }
                sb.deleteCharAt(sb.lastIndexOf(SEP));

            }
            sb.append(RIGHT_TAG);

        } catch (Exception e) {
            LOGGER.error("construct application log error!", e);
        }

        return sb.toString();
    }


    /**
     * 构造响应信息
     *
     * @param invocationSignature 方法签名
     * @param result              结果
     * @param elapseTime          耗时
     * @return 响应信息
     */
    private String buildResponseInfo(String invocationSignature, Object result, long elapseTime) {

        StringBuilder sb = new StringBuilder(100);
        sb.append("(");

        // 调用方法签名："接口名.方法名"
        sb.append(invocationSignature).append(SEP);

        // 调用结果
        if (result instanceof Boolean) {
            sb.append((Boolean) result ? "Y" : "N").append(SEP);

        } else if (result instanceof AbstractAccountResult) {
            AbstractAccountResult rst = (AbstractAccountResult) result;
            String msgCode = rst.getMsgCode();
            String successFlag = CodeEnum.SUCCESS.getCode().equals(msgCode) ? "Y" : "N";
            sb.append(successFlag).append(SEP);
            sb.append(msgCode).append(SEP);

        } else {
            sb.append(DEFAULT_STR).append(SEP);
            sb.append(CodeEnum.SYSTEM_EXCEPTION.getCode()).append(SEP);

        }

        // 方法耗时
        sb.append(elapseTime).append("ms");
        sb.append(")");

        return sb.toString();
    }

}