package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.AccountTypeCriteria;
import net.n2oapp.security.admin.api.model.AccountType;
import net.n2oapp.security.admin.api.service.AccountTypeService;
import net.n2oapp.security.admin.rest.api.criteria.AccountTypeRestCriteria;
import net.n2oapp.security.admin.rest.client.feign.AccountTypeFeignClient;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления ролями, для вызова rest
 */
public class AccountTypeRestClient implements AccountTypeService {

    private AccountTypeFeignClient client;

    public AccountTypeRestClient(AccountTypeFeignClient client) {
        this.client = client;
    }

    @Override
    public Page<AccountType> findAll(AccountTypeCriteria criteria) {
        AccountTypeRestCriteria restCriteria = new AccountTypeRestCriteria();
        restCriteria.setUserLevel(criteria.getUserLevel());
        restCriteria.setName(criteria.getName());
        restCriteria.setPage(criteria.getPage());
        restCriteria.setOrders(criteria.getOrders());
        restCriteria.setSize(criteria.getSize());
        return client.findAll(restCriteria);
    }

    @Override
    public AccountType findById(Integer id) {
        return client.findById(id);
    }

    @Override
    public AccountType create(AccountType accountType) {
        return client.create(accountType);
    }

    @Override
    public AccountType update(AccountType accountType) {
        return client.update(accountType);
    }

    @Override
    public void delete(Integer id) {
        client.delete(id);
    }
}
