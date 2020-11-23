package com.aliyun.gts.financial.showcases.sofa.handler;

import com.alibaba.common.lang.StringUtil;
import com.aliyun.gts.financial.showcases.sofa.facade.exception.TradeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Component
@Provider
public class SofaRestExceptionHandler implements ExceptionMapper<Throwable> {

    private static final Logger logger = LoggerFactory
            .getLogger(SofaRestExceptionHandler.class);


    public static final String ERROR_CODE_KEY = "code";
    public static final String MESSAGES_KEY = "messages";
    private static final int ERROR_STATUS = 451;

    @Override
    public Response toResponse(Throwable ex) {
        logger.error("get ex: {}", ex.getMessage());
        String errorCode = "NO Common Error Code!!";
        String message = "";

        if (ex instanceof TradeException) {
            TradeException casted = (TradeException) ex;
            errorCode = StringUtil.isBlank(casted.getErrorCode()) ? errorCode : casted
                    .getErrorCode();
            message = casted.getMessage();

        } else {
            errorCode = "NO Common Error Code!!";
            message = MessageFormat.format("Unknown Exception[{0}]", ex.getMessage());
        }

        logger.error("web api error!", ex);
        logger.error("errorCode={0}, message={1}", errorCode, message);

        Map<String, String> errorMap = new HashMap<String, String>();
        errorMap.put(ERROR_CODE_KEY, errorCode);
        errorMap.put(MESSAGES_KEY, message);
        return Response.status(ERROR_STATUS).entity(errorMap).type(MediaType.APPLICATION_JSON).build();
    }
}
