package com.aliyun.gts.financial.showcases.sofa.tcc;

import java.sql.SQLException;
import java.util.Map;

import com.alipay.dtx.client.core.api.domain.BusinessActionContext;
import com.alipay.dtx.client.core.spi.BusinessActionContextParameter;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.aliyun.gts.financial.showcases.sofa.dao.AccountPointDAO;
import com.aliyun.gts.financial.showcases.sofa.dynamic.PointConfig;
import com.aliyun.gts.financial.showcases.sofa.facade.model.AccountPoint;
import com.aliyun.gts.financial.showcases.sofa.facade.tcc.PointReturnTccService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@SofaService(bindings = { @SofaServiceBinding(bindingType = "bolt") })
public class PointReturnTccServiceImpl implements PointReturnTccService {
	private final static Logger LOGGER = LoggerFactory.getLogger(PointReturnTccServiceImpl.class);

	@Autowired
	private AccountPointDAO accountPointDAO;

	@Autowired
	PointConfig pointConfig;

	@Override
	public boolean addPoint(BusinessActionContext businessActionContext, String accountNo) {

		// 动态配置
		double pointToAdd = pointConfig.getPointValue();
		LOGGER.info("current point to add {} for account {}", pointToAdd, accountNo);

		try {
			AccountPoint accountPoint = accountPointDAO.findAccountPoint(accountNo);
			if (accountPoint == null) {
				AccountPoint newAccountPoint = new AccountPoint();
				newAccountPoint.setPoint(pointToAdd);
				newAccountPoint.setAccountNo(accountNo);
				newAccountPoint.setStatus(0);
				accountPointDAO.addAccountPoint(newAccountPoint);
			} else {
				double newPoint = accountPoint.getPoint() + pointToAdd;
				accountPoint.setPoint(newPoint);
				accountPoint.setStatus(0);
				accountPointDAO.updateAccountPoint(accountPoint);
			}

			return true;
		} catch (Exception e) {
			LOGGER.error("add point failed {}", e.getMessage(), e);
		}
		return false;
	}

	@Override
	public boolean commit(BusinessActionContext businessActionContext) {
		// TCC二阶段传值：演示通过BusinessActionContext进行传递
    	// 2. 在二阶段方法中，通过以下方式去获取目标参数值
		String accountNo = (String) businessActionContext.getActionContext().get("accountNo");
		LOGGER.info("commit account {}", accountNo);

		try {
			AccountPoint accountPoint = accountPointDAO.findAccountPoint(accountNo);
			if (accountPoint == null) {
				return false;
			}
			accountPoint.setStatus(1);
			accountPointDAO.updateAccountPoint(accountPoint);
			return true;
		} catch (Exception e) {
			LOGGER.error("commit failed {}", e.getMessage(), e);
		}
		return false;
	}

	@Override
	public boolean rollback(BusinessActionContext businessActionContext) {
		// TCC二阶段传值：演示通过BusinessActionContext进行传递
    	// 2. 在二阶段方法中，通过以下方式去获取目标参数值
		String accountNo = (String) businessActionContext.getActionContext().get("accountNo");
		LOGGER.info("rollback account {}", accountNo);

		try {
			AccountPoint accountPoint = accountPointDAO.findAccountPoint(accountNo);
			if (accountPoint == null) {
				return false;
			}
			accountPoint.setStatus(2);
			accountPointDAO.updateAccountPoint(accountPoint);
			return true;
		} catch (Exception e) {
			LOGGER.error("rollback failed {}", e.getMessage(), e);
		}
		return false;
	}
}