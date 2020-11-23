package com.aliyun.gts.financial.showcases.sofa.facade.response;

/**
 * Response data wrapper in the format of json.
 */
public class RestObjResp<T> extends AbstractResp {

    private T data;


    public RestObjResp() {
        super(true);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RestSampleFacadeResp{" +
                "data=" + data +
                '}';
    }
}
