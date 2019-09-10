package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.ApplicationCriteria;
import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.api.service.ApplicationSystemService;
import net.n2oapp.security.admin.rest.api.ApplicationSystemRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestApplicationCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestSystemCriteria;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления ролями, для вызова rest
 */
public class ApplicationSystemServiceRestClient implements ApplicationSystemService {

    private ApplicationSystemRestService client;

    public ApplicationSystemServiceRestClient(ApplicationSystemRestService client) {
        this.client = client;
    }

    @Override
    public Application createApplication(Application system) {
        return client.createApplication(system);
    }

    @Override
    public Application updateApplication(Application system) {
        return client.updateApplication(system);
    }

    @Override
    public void deleteApplication(String code) {
        client.deleteApplication(code);
    }

    @Override
    public Application getApplicationById(String id) {
        return client.getApplicationById(id);
    }

    @Override
    public Page<Application> findAllApplications(ApplicationCriteria criteria) {
        RestApplicationCriteria serviceCriteria = new RestApplicationCriteria();
        serviceCriteria.setPage(criteria.getPageNumber());
        serviceCriteria.setSize(criteria.getPageSize());
        serviceCriteria.setSystemCode(criteria.getSystemCode());
        serviceCriteria.setOrders(criteria.getOrders());
        return client.findAllApplications(serviceCriteria);
    }

    @Override
    public Boolean isApplicationExist(String code) {
        return client.getApplicationById(code) != null;
    }

    @Override
    public AppSystem createSystem(AppSystemForm system) {
        return client.createSystem(system);
    }

    @Override
    public AppSystem updateSystem(AppSystemForm system) {
        return client.updateSystem(system);
    }

    @Override
    public void deleteSystem(String code) {
        client.deleteSystem(code);
    }

    @Override
    public AppSystem getSystemById(String id) {
        return client.getSystemById(id);
    }

    @Override
    public Page<AppSystem> findAllSystems(SystemCriteria criteria) {
        RestSystemCriteria systemCriteria = new RestSystemCriteria();
        systemCriteria.setPage(criteria.getPageNumber());
        systemCriteria.setSize(criteria.getPageSize());
        systemCriteria.setName(criteria.getName());
        systemCriteria.setCode(criteria.getCode());
        systemCriteria.setOrders(criteria.getOrders());
        return client.findAllSystems(systemCriteria);
    }

    @Override
    public Boolean isSystemExist(String code) {
        return client.getSystemById(code) != null;
    }
}
