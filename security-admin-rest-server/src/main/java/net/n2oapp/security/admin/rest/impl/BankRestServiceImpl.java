package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.bank.Bank;
import net.n2oapp.security.admin.api.model.bank.BankCreateForm;
import net.n2oapp.security.admin.api.model.bank.BankUpdateForm;
import net.n2oapp.security.admin.api.service.BankService;
import net.n2oapp.security.admin.rest.api.BankRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestBankCriteria;
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
    public Page<Bank> findAll(RestBankCriteria criteria) {
        return service.findAll(criteria);
    }

    @Override
    public Bank getById(String id) {
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
