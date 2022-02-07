package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.AccountCriteria;
import net.n2oapp.security.admin.api.model.Account;
import net.n2oapp.security.admin.api.service.AccountService;
import net.n2oapp.security.admin.rest.api.AccountRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestAccountCriteria;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления аккаунтами, для вызова rest
 */
public class AccountServiceRestClient implements AccountService {

    private AccountRestService client;

    public AccountServiceRestClient(AccountRestService client) {
        this.client = client;
    }

    @Override
    public Page<Account> findAll(AccountCriteria criteria) {
        RestAccountCriteria restCriteria = new RestAccountCriteria();
        restCriteria.setUserId(criteria.getUserId());
        restCriteria.setPage(criteria.getPage());
        restCriteria.setOrders(criteria.getOrders());
        restCriteria.setSize(criteria.getSize());
        return client.findAll(restCriteria);
    }

    @Override
    public Account getById(Integer id) {
        return client.findById(id);
    }

    @Override
    public Account create(Account account) {
        return client.create(account);
    }

    @Override
    public Account update(Account account) {
        return client.update(account);
    }

    @Override
    public void delete(Integer id) {
        client.delete(id);
    }

    @Override
    public Account changeActive(Integer id) {
        return client.changeActive(id);
    }
}
