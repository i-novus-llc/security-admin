package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.BankCriteria;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.model.bank.Bank;
import net.n2oapp.security.admin.api.model.bank.BankCreateForm;
import net.n2oapp.security.admin.api.model.bank.BankUpdateForm;
import net.n2oapp.security.admin.api.service.BankService;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.rest.api.BankRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import org.springframework.data.domain.Page;

import java.util.UUID;

/**
 * Прокси реализация сервиса управления пользователями, для вызова rest
 */
public class BankServiceRestClient implements BankService {

    private BankRestService client;

    public BankServiceRestClient(BankRestService client) {
        this.client = client;
    }

    @Override
    public Page<Bank> search(BankCriteria criteria) {
        return client.findAll(criteria);
    }

    @Override
    public Bank getById(UUID id) {
        return client.getById(id);
    }

    @Override
    public Bank create(BankCreateForm bank) {
        return client.create(bank);
    }

    @Override
    public Bank update(BankUpdateForm bank) {
        return client.update(bank);
    }
}
