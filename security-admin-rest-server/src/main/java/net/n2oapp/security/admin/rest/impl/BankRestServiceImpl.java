package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.criteria.BankCriteria;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.model.bank.Bank;
import net.n2oapp.security.admin.api.model.bank.BankCreateForm;
import net.n2oapp.security.admin.api.model.bank.BankUpdateForm;
import net.n2oapp.security.admin.api.service.BankService;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.rest.api.BankRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestUserDetailsToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

import java.util.UUID;


/**
 * Реализация REST сервиса управления сведениями о банке
 */
@Controller
public class BankRestServiceImpl implements BankRestService {

    @Autowired
    private BankService service;


    @Override
    public Page<Bank> findAll(BankCriteria criteria) {
        return service.search(criteria);
    }

    @Override
    public Bank getById(UUID id) {
        return service.getById(id);
    }

    @Override
    public Bank create(BankCreateForm bank) {
        return service.create(bank);
    }

    @Override
    public Bank update(BankUpdateForm bank) {
        return service.update(bank);
    }
}
