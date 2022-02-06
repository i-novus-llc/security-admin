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
    private AccountService service;

    @Override
    public Page<Account> findAll(RestAccountCriteria criteria) {
        return service.findAll(criteria);
    }

    @Override
    public Account findById(Integer id) {
        return service.getById(id);
    }

    @Override
    public Account create(Account account) {
        return service.create(account);
    }

    @Override
    public Account update(Account account) {
        return service.update(account);
    }

    @Override
    public void delete(Integer id) {
        service.delete(id);
    }

    @Override
    public Account changeActive(Integer id) {
        return service.changeActive(id);
    }
}
