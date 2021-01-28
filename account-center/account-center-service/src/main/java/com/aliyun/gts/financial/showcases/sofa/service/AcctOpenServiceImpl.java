package com.aliyun.gts.financial.showcases.sofa.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.aliyun.gts.financial.showcases.sofa.dao.AccountDAO;
import com.aliyun.gts.financial.showcases.sofa.facade.api.AcctOpenService;
import com.aliyun.gts.financial.showcases.sofa.facade.model.Account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@SofaService(bindings = { @SofaServiceBinding(bindingType = "bolt") })
public class AcctOpenServiceImpl implements AcctOpenService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcctOpenServiceImpl.class);

    @Autowired
    private AccountDAO accountDAO;

    @Override
    public Boolean initAccounts(String magicNumber) {
        validateMagicNumber(magicNumber);

        int workerCount = 10;

        ExecutorService threadPool = Executors.newFixedThreadPool(workerCount);

        // 初始化一万条数据，格式为：00~99 + magicNumber + 00~99
        for (int i = 0; i < workerCount; i++) {
            int start = i*10;
            int end = start + 9;
            threadPool.execute(() -> {
                try {
                    batchInsertAccounts(start, end, magicNumber);
                    LOGGER.info("init accounts between [{},{}] success", start, end);
                } catch (Exception e) {
                    LOGGER.error("init accounts between [{},{}] failed: {}", start, end, e.getMessage(), e);
                }
            });
        }

        return true;
    }

    private void batchInsertAccounts(int start, int end, String magicNumber) {
        List<Account> accounts = new ArrayList<Account>();
        for (int i = start; i <= end; i++) {
            String prefix = end < 10 ? ("0" + i) : String.valueOf(i);
            for (int j = 0; j < 100; j++) {
                String suffix = j < 10 ? "0" + j : String.valueOf(j);
                Account account = new Account();
                account.setAccountNo(prefix + magicNumber + suffix);
                account.setBalance(new BigDecimal(10000));
                account.setFreezeAmount(BigDecimal.ZERO);
                account.setUnreachAmount(BigDecimal.ZERO);
                accounts.add(account);
            }
        }
        accountDAO.batchInsertAccounts(accounts);
    }

    private void validateMagicNumber(String magicNumber) {
        if (null == magicNumber) {
            throw new RuntimeException("Magic number is null!");
        }

        if (magicNumber.length() != 4) {
            throw new RuntimeException("Length of magic number must equal 4!");
        }

        if (!isNumeric(magicNumber)) {
            throw new RuntimeException("Magic number must be numbers only!");
        }

        Account account = accountDAO.getAccount("00"+magicNumber+"00");
        if (null != account) {
            throw new RuntimeException("Magic number has existed already!");
        }
    }

    private boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher match = pattern.matcher(str);

        return match.matches();
    }
}