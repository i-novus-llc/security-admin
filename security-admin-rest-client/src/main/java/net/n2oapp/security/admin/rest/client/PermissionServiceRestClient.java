package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.BaseCriteria;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.rest.api.PermissionRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestBaseCriteria;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления правами доступа, для вызова rest
 */
public class PermissionServiceRestClient implements PermissionService {

    private PermissionRestService client;

    public PermissionServiceRestClient(PermissionRestService client) {
        this.client = client;
    }

    @Override
    public Permission create(Permission permission) {
        return client.create(permission);
    }

    @Override
    public Permission update(Permission permission) {
        return client.update(permission);
    }

    @Override
    public void delete(Integer id) {
        client.delete(id);
    }

    @Override
    public Permission getById(Integer id) {
        return client.getById(id);
    }

    @Override
    public Page<Permission> findAll(BaseCriteria criteria) {
        RestBaseCriteria baseCriteria = new RestBaseCriteria();
        baseCriteria.setPage(criteria.getPageNumber());
        baseCriteria.setSize(criteria.getPageSize());
        baseCriteria.setOrders(criteria.getOrders());
        return client.findAll(baseCriteria);
    }
}
