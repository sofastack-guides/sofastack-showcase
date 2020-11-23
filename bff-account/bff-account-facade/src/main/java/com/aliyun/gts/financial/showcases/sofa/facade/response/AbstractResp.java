package com.aliyun.gts.financial.showcases.sofa.facade.response;

/**
 * Common response data.
 */
public class AbstractResp {

    private boolean success = false;

    private String resultMsg;

    private String requestId;

    public AbstractResp(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
