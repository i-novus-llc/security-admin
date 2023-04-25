package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import net.n2oapp.security.admin.api.service.SystemService;
import net.n2oapp.security.admin.rest.api.criteria.RestSystemCriteria;
import net.n2oapp.security.admin.rest.client.feign.SystemServiceFeignClient;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления ролями, для вызова rest
 */
public class SystemServiceRestClient implements SystemService {

    private SystemServiceFeignClient client;

    public SystemServiceRestClient(SystemServiceFeignClient client) {
        this.client = client;
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
    public AppSystem getSystem(String id) {
        return client.getSystem(id);
    }

    @Override
    public Page<AppSystem> findAllSystems(SystemCriteria criteria) {
        RestSystemCriteria systemCriteria = new RestSystemCriteria();
        systemCriteria.setPage(criteria.getPageNumber());
        systemCriteria.setSize(criteria.getPageSize());
        systemCriteria.setName(criteria.getName());
        systemCriteria.setCode(criteria.getCode());
        systemCriteria.setOrders(criteria.getOrders());
        systemCriteria.setCodeList(criteria.getCodeList());
        systemCriteria.setPublicAccess(criteria.getPublicAccess());
        return client.findAllSystems(systemCriteria);
    }

    @Override
    public Boolean isSystemExist(String code) {
        return client.getSystem(code) != null;
    }
}
