package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.Account;
import net.n2oapp.security.admin.api.service.AccountService;
import net.n2oapp.security.admin.rest.api.AccountRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestAccountCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

@Controller
public class AccountRestServiceImpl implements AccountRestService {
    @Autowired
    private AccountService accountService;

    @Override
    public Page<Account> findAll(RestAccountCriteria criteria) {
        return accountService.findAll(criteria);
    }

    @Override
    public Account findById(Integer accountId) {
        return accountService.getById(accountId);
    }

    @Override
    public Account create(Account account) {
        return accountService.create(account);
    }

    @Override
    public Account update(Account account) {
        return accountService.update(account);
    }

    @Override
    public void delete(Integer accountId) {
        accountService.delete(accountId);
    }
}
