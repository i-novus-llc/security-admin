package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.BankCriteria;
import net.n2oapp.security.admin.api.model.bank.Bank;
import net.n2oapp.security.admin.api.model.bank.BankCreateForm;
import net.n2oapp.security.admin.api.model.bank.BankUpdateForm;
import net.n2oapp.security.admin.api.service.BankService;
import net.n2oapp.security.admin.rest.api.BankRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestBankCriteria;
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
    public Page<Bank> findAll(BankCriteria criteria) {
        RestBankCriteria restCriteria = new RestBankCriteria();
        restCriteria.setPage(criteria.getPageNumber());
        restCriteria.setSize(criteria.getPageSize());
        restCriteria.setName(criteria.getName());
        restCriteria.setParent(criteria.getParent());
        restCriteria.setType(criteria.getType());
        restCriteria.setOrders(criteria.getOrders());
        return client.findAll(restCriteria);
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
