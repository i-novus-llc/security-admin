package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.ServiceCriteria;
import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.api.model.AppService;
import net.n2oapp.security.admin.api.model.AppServiceForm;
import net.n2oapp.security.admin.api.service.AppServiceService;
import net.n2oapp.security.admin.rest.api.AppServiceRestService;
import net.n2oapp.security.admin.rest.api.SystemRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestServiceCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestSystemCriteria;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления ролями, для вызова rest
 */
public class AppServiceServiceRestClient implements AppServiceService {

    private AppServiceRestService client;

    public AppServiceServiceRestClient(AppServiceRestService client) {
        this.client = client;
    }

    @Override
    public AppService create(AppServiceForm system) {
        return client.create(system);
    }

    @Override
    public AppService update(AppServiceForm system) {
        return client.update(system);
    }

    @Override
    public void delete(String code) {
        client.delete(code);
    }

    @Override
    public AppService getById(String id) {
        return client.getById(id);
    }

    @Override
    public Page<AppService> findAll(ServiceCriteria criteria) {
        RestServiceCriteria serviceCriteria = new RestServiceCriteria();
        serviceCriteria.setPage(criteria.getPageNumber());
        serviceCriteria.setSize(criteria.getPageSize());
        serviceCriteria.setSystemCode(criteria.getSystemCode());
        serviceCriteria.setOrders(criteria.getOrders());
        return client.findAll(serviceCriteria);
    }

    @Override
    public Boolean isServiceExist(String code) {
        return client.getById(code) != null;
    }
}
