package com.aliyun.gts.financial.showcases.sofa.facade.consts;

import javax.ws.rs.core.MediaType;

public class RestConsts {

    /**
     * rest prefix address
     */
    public static final String REST_API_PEFFIX = "/webapi";

     /**
     * response encoding
     */
    public static final String DEFAULT_CHARSET = "UTF-8";
    
    /**
     * Content-Type
     */
    public static final String DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_JSON + ";" + MediaType.CHARSET_PARAMETER + "=" + DEFAULT_CHARSET;

}