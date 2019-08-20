package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.ApplicationCriteria;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.api.model.ApplicationForm;
import net.n2oapp.security.admin.api.service.ApplicationService;
import net.n2oapp.security.admin.rest.api.ApplicationRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestApplicationCriteria;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления ролями, для вызова rest
 */
public class ApplicationServiceRestClient implements ApplicationService {

    private ApplicationRestService client;

    public ApplicationServiceRestClient(ApplicationRestService client) {
        this.client = client;
    }

    @Override
    public Application create(ApplicationForm system) {
        return client.create(system);
    }

    @Override
    public Application update(ApplicationForm system) {
        return client.update(system);
    }

    @Override
    public void delete(String code) {
        client.delete(code);
    }

    @Override
    public Application getById(String id) {
        return client.getById(id);
    }

    @Override
    public Page<Application> findAll(ApplicationCriteria criteria) {
        RestApplicationCriteria serviceCriteria = new RestApplicationCriteria();
        serviceCriteria.setPage(criteria.getPageNumber());
        serviceCriteria.setSize(criteria.getPageSize());
        serviceCriteria.setSystemCode(criteria.getSystemCode());
        serviceCriteria.setOrders(criteria.getOrders());
        return client.findAll(serviceCriteria);
    }

    @Override
    public Boolean isApplicationExist(String code) {
        return client.getById(code) != null;
    }
}
