package com.aliyun.gts.financial.showcases.sofa.facade.rest;

import java.math.BigDecimal;

import javax.ws.rs.*;

import com.aliyun.gts.financial.showcases.sofa.facade.consts.RestConsts;
import com.aliyun.gts.financial.showcases.sofa.facade.exception.TradeException;
import com.aliyun.gts.financial.showcases.sofa.facade.model.Trade;
import com.aliyun.gts.financial.showcases.sofa.facade.request.TransferTradeRequest;
import com.aliyun.gts.financial.showcases.sofa.facade.response.RestObjResp;

@Path(RestConsts.REST_API_PEFFIX + "/account")
@Consumes(RestConsts.DEFAULT_CONTENT_TYPE)
@Produces(RestConsts.DEFAULT_CONTENT_TYPE)
public interface AccountRestService {

    @GET
    @Path("/balance/{accountNo}")
    RestObjResp<BigDecimal> getAccountBalance(@PathParam("accountNo") String accountNo) throws TradeException;

    @GET
    @Path("/point/{accountNo}")
    RestObjResp<Double> getPoint(@PathParam("accountNo") String accountNo) throws TradeException;

    @POST
    @Path("/transfer/{dtxType}")
    RestObjResp<Trade> transfer(@PathParam("dtxType") String dtxType, TransferTradeRequest transferTradeRequest)
            throws TradeException;

    @GET
    @Path("/init/{magicNumber}")
    RestObjResp<Boolean> initAccounts(@PathParam("magicNumber") String magicNumber) throws TradeException;
}