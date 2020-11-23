package com.aliyun.gts.financial.showcases.sofa.rest;

import java.math.BigDecimal;
import java.util.Date;

import com.alipay.sofa.rpc.api.GenericService;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.aliyun.gts.financial.showcases.sofa.facade.api.AcctOpenService;
import com.aliyun.gts.financial.showcases.sofa.facade.api.AcctQueryService;
import com.aliyun.gts.financial.showcases.sofa.facade.api.RandomFailService;
import com.aliyun.gts.financial.showcases.sofa.facade.bolt.AccountBoltService;
import com.aliyun.gts.financial.showcases.sofa.facade.exception.TradeException;
import com.aliyun.gts.financial.showcases.sofa.facade.model.Trade;
import com.aliyun.gts.financial.showcases.sofa.facade.request.AccountTransRequest;
import com.aliyun.gts.financial.showcases.sofa.facade.request.TransferTradeRequest;
import com.aliyun.gts.financial.showcases.sofa.facade.response.RestObjResp;
import com.aliyun.gts.financial.showcases.sofa.facade.rest.AccountRestService;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountQueryResult;
import com.aliyun.gts.financial.showcases.sofa.facade.result.AccountTransResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

// RPC演示：注解方式暴露REST协议的RPC服务
@SofaService(bindings = { @SofaServiceBinding(bindingType = "rest") })
public class AccountRestServiceImpl implements AccountRestService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AccountRestServiceImpl.class);

    // RPC演示：注解方式引用BOLT服务接口
    @SofaReference(interfaceType = AcctQueryService.class, binding = @SofaReferenceBinding(bindingType = "bolt"))
    AcctQueryService acctQueryService;

    @SofaReference(interfaceType = AcctOpenService.class, binding = @SofaReferenceBinding(bindingType = "bolt", timeout = 30000))
    AcctOpenService acctOpenService;

    @SofaReference(interfaceType = RandomFailService.class, binding = @SofaReferenceBinding(bindingType = "bolt"))
    RandomFailService randomFailService;

    // RPC演示：泛化调用
    @Autowired
    GenericService pointGenericService;

    @Autowired
    AccountBoltService accountBoltService;

    @Override
    public RestObjResp<BigDecimal> getAccountBalance(String accountNo) throws TradeException {
        LOGGER.info("To get account balance from [{}]", accountNo);
        RestObjResp<BigDecimal> response = new RestObjResp<BigDecimal>();

        try {
            AccountQueryResult accQueryResult = acctQueryService.queryAccount(accountNo);
            BigDecimal balance = accQueryResult.getAccount().getBalance();
            LOGGER.info("Account balance for [{}] is [{}]", accountNo, balance);

            response.setData(balance);
            response.setSuccess(true);
            response.setResultMsg("SUCCESS");
        } catch (Exception e) {
            LOGGER.error("get account balance failed: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setResultMsg(e.getMessage());
        }

        return response;
    }

    @Override
    public RestObjResp<Trade> transfer(String dtxType, TransferTradeRequest transferTradeRequest)
            throws TradeException {

        LOGGER.info("transfer via rest start.....dtx type is [{}]", dtxType);

        AccountTransRequest accountTransRequest = convert(transferTradeRequest);

        AccountTransResult accountTransResult = null;
        try {
            accountTransResult = accountBoltService.transerTrade(dtxType, accountTransRequest);
        } catch (Exception e) {
            LOGGER.error("transfer trade failed: {}", e.getMessage(), e);
            accountTransResult = AccountTransResult.failedResultOf("TRADE_FAIL", "transfer trade failed");
        }

        RestObjResp<Trade> result = new RestObjResp<Trade>();
        result.setSuccess(accountTransResult.isSuccess());
        result.setResultMsg(accountTransResult.getMsgText());
        result.setRequestId(accountTransResult.getTransLogId());
        return result;
    }

    @Override
    public RestObjResp<Double> getPoint(String accountNo) throws TradeException {

        RestObjResp<Double> response = new RestObjResp<Double>();

        try {
            LOGGER.info("get point via rpc-generic for account: [{}]", accountNo);
            // RPC演示：泛化调用，通过$inovke来调用具体的方法，并传入参数
            double point = (double) pointGenericService.$invoke("getPoint", new String[] { String.class.getName() },
                    new Object[] { accountNo });

            response.setSuccess(true);
            response.setData(point);
            response.setResultMsg("SUCCESS");
        } catch (Exception e) {
            LOGGER.error("get point via rpc-generic failed: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setResultMsg(e.getMessage());
        }

        return response;
    }

    @Override
    public RestObjResp<Boolean> initAccounts(String magicNumber) throws TradeException {
        RestObjResp<Boolean> response = new RestObjResp<Boolean>();

        try {
            LOGGER.info("initialize accounts...");
            // RPC演示：泛化调用，通过$inovke来调用具体的方法，并传入参数
            boolean result = acctOpenService.initAccounts(magicNumber);

            response.setSuccess(true);
            response.setData(result);
            response.setResultMsg("SUCCESS");
        } catch (Exception e) {
            LOGGER.error("initialize accounts failed: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setResultMsg(e.getMessage());
        }

        return response;
    }

    @Override
    public RestObjResp<Integer> randomFail() throws TradeException {
        RestObjResp<Integer> response = new RestObjResp<Integer>();

        try {
            LOGGER.info("random fail testing...");
            // RPC演示：泛化调用，通过$inovke来调用具体的方法，并传入参数
            int result = randomFailService.run();

            response.setSuccess(true);
            response.setData(result);
            response.setResultMsg("SUCCESS");
        } catch (Exception e) {
            LOGGER.error("random fail {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setResultMsg(e.getMessage());
        }

        return response;
    }

    private AccountTransRequest convert(TransferTradeRequest transferTradeRequest) {
        AccountTransRequest accountTransRequest = new AccountTransRequest();
        accountTransRequest.setBacc(transferTradeRequest.getFromAccountNo());
        accountTransRequest.setPeerBacc(transferTradeRequest.getToAccountNo());
        accountTransRequest.setTxnAmt(transferTradeRequest.getTransferAmount());
        accountTransRequest.setTxnTime(new Date());
        return accountTransRequest;
    }

    

}