package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.AccountType;
import net.n2oapp.security.admin.api.service.AccountTypeService;
import net.n2oapp.security.admin.rest.api.AccountTypeRestService;
import net.n2oapp.security.admin.rest.api.criteria.AccountTypeRestCriteria;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

@Controller
public class AccountTypeRestServiceImpl implements AccountTypeRestService {

    private final AccountTypeService service;

    public AccountTypeRestServiceImpl(AccountTypeService service) {
        this.service = service;
    }

    @Override
    public Page<AccountType> findAll(AccountTypeRestCriteria criteria) {
        return service.findAll(criteria);
    }

    @Override
    public AccountType findById(Integer accountTypeId) {
        return service.findById(accountTypeId);
    }

    @Override
    public AccountType create(AccountType accountType) {
        return service.create(accountType);
    }

    @Override
    public AccountType update(AccountType accountType) {
        return service.update(accountType);
    }

    @Override
    public void delete(Integer accountTypeId) {
        service.delete(accountTypeId);
    }
}
