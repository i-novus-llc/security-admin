package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import net.n2oapp.security.admin.api.service.AppSystemService;
import net.n2oapp.security.admin.rest.api.SystemRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestSystemCriteria;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления ролями, для вызова rest
 */
public class AppSystemServiceRestClient implements AppSystemService {

    private SystemRestService client;

    public AppSystemServiceRestClient(SystemRestService client) {
        this.client = client;
    }

    @Override
    public AppSystem create(AppSystemForm system) {
        return client.create(system);
    }

    @Override
    public AppSystem update(AppSystemForm system) {
        return client.update(system);
    }

    @Override
    public void delete(String code) {
        client.delete(code);
    }

    @Override
    public AppSystem getById(String id) {
        return client.getById(id);
    }

    @Override
    public Page<AppSystem> findAll(SystemCriteria criteria) {
        RestSystemCriteria systemCriteria = new RestSystemCriteria();
        systemCriteria.setPage(criteria.getPageNumber());
        systemCriteria.setSize(criteria.getPageSize());
        systemCriteria.setName(criteria.getName());
        systemCriteria.setCode(criteria.getCode());
        systemCriteria.setOrders(criteria.getOrders());
        return client.findAll(systemCriteria);
    }

    @Override
    public Boolean isSystemExist(String code) {
        return client.getById(code) != null;
    }
}
