package com.aliyun.gts.financial.showcases.sofa.api;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.aliyun.gts.financial.showcases.sofa.dao.AccountPointDAO;
import com.aliyun.gts.financial.showcases.sofa.facade.api.PointQueryService;
import com.aliyun.gts.financial.showcases.sofa.facade.model.AccountPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

// 泛化调用：服务端跟常规方式一样，没有区别
@SofaService(bindings = {@SofaServiceBinding(bindingType = "bolt")})
public class PointQueryServiceImpl implements PointQueryService {

    private final static Logger LOGGER = LoggerFactory.getLogger(PointQueryServiceImpl.class);

	@Autowired
	private AccountPointDAO accountPointDAO;

    @Override
    public double getPoint(String accountNo) {
        LOGGER.info("query point for account: [{}]", accountNo);
        AccountPoint accountPoint = accountPointDAO.findAccountPoint(accountNo);
        if (accountPoint == null) {
            return -1;
        }
        return accountPoint.getPoint();
    }
    
}