package com.aliyun.gts.financial.showcases.sofa.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

import com.alipay.sofa.common.profile.diagnostic.Profiler;

public class DalDigestLogInterceptor implements MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DalDigestLogInterceptor.class);

    /**
     * 分隔符
     */
    public static final String SEP = ",";

    /**
     * 点分割符
     */
    public static final String DOT_SEP = ".";

    /**
     * 默认值
     */
    public static final String DEFAULT_STR = "-";

    /**
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        // 被拦截的DAO方法
        Method method = invocation.getMethod();

        // 被拦截方法签名："类名.方法名"
        String invocationSignature = method.getDeclaringClass().getSimpleName() + "."
                + method.getName();

        // DAO执行是否成功
        boolean isSuccess = true;

        long startTime = System.currentTimeMillis();

        try {

            return invocation.proceed();

        } catch (Exception e) {

            isSuccess = false;
            throw e;

        } finally {

            Profiler.release();

            try {

                long elapseTime = System.currentTimeMillis() - startTime;

                Object[] arguments = invocation.getArguments();

                boolean result = isSuccess;

                LOGGER.info(buildDigestLog(invocationSignature, result, elapseTime, arguments));

            } catch (Exception e) {
                LOGGER.error("Record dal digest log error", e);
            }

        }

    }

    /**
     * 构造摘要日志
     *
     * @param invocationSignature
     * @param isSuccess
     * @param elapseTime
     * @param arguments
     * @return
     */
    private String buildDigestLog(String invocationSignature, boolean isSuccess, long elapseTime,
                                  Object[] arguments) {

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        {
            //打印主体信息
            sb.append("(");

            // 调用方法签名："系统名.方法名"
            sb.append(invocationSignature).append(SEP);

            // 方法执行是否成功
            sb.append(isSuccess ? "Y" : "N").append(SEP);

            // 方法耗时
            sb.append(elapseTime).append("ms");

            sb.append(")");
        }

        {
            if (arguments != null) {

                // 打印参数信息
                sb.append("(dbp,");

                for (Object arg : arguments) {

                    if (arg == null) {
                        sb.append(DEFAULT_STR).append(SEP);
                        continue;
                    }

                    // 打印请求信息
                    if (arg instanceof List) {

                        @SuppressWarnings("unchecked")
                        List<Object> list = (List<Object>) arg;

                        sb.append(getListString(list)).append(SEP);

                    } else {
                        sb.append(arg).append(SEP);
                    }

                }

                if (arguments.length > 0) {
                    sb.deleteCharAt(sb.lastIndexOf(SEP));
                }

                sb.append(")");
            }

        }
        sb.append("]");

        return sb.toString();

    }

    /**
     * 获得List的字符串形式
     *
     * @param list
     * @return List的字符串形式
     */
    private String getListString(List<Object> list) {

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (Object obj : list) {
            sb.append(obj).append(SEP);
        }

        if (list.size() > 0) {
            sb.deleteCharAt(sb.lastIndexOf(SEP));
        }
        sb.append("]");

        return sb.toString();
    }

}