package com.aliyun.gts.financial.showcases.sofa.facade.result;

import com.aliyun.gts.financial.showcases.sofa.facade.model.Account;

public class AccountQueryResult extends AbstractAccountResult {

    private Account account;

    public Account getAccount() {
        return this.account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
    
}