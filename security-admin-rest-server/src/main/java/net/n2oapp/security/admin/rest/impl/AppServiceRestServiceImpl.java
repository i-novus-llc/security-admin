package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.criteria.ServiceCriteria;
import net.n2oapp.security.admin.api.model.AppService;
import net.n2oapp.security.admin.api.model.AppServiceForm;
import net.n2oapp.security.admin.api.service.AppServiceService;
import net.n2oapp.security.admin.rest.api.AppServiceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;


/**
 * Реализация REST сервиса управления службами
 */
@Controller
public class AppServiceRestServiceImpl implements AppServiceRestService {
    @Autowired
    private AppServiceService service;

    @Override
    public Page<AppService> findAll(ServiceCriteria criteria) {
        return service.findAll(criteria);
    }

    @Override
    public AppService getById(String code) {
        return service.getById(code);
    }

    @Override
    public AppService create(AppServiceForm systemForm) {
        return service.create(systemForm);
    }

    @Override
    public AppService update(AppServiceForm systemForm) {
        return service.update(systemForm);

    }

    @Override
    public void delete(String code) {
        service.delete(code);
    }

}
