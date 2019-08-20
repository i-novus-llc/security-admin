package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.api.model.ApplicationForm;
import net.n2oapp.security.admin.api.service.ApplicationService;
import net.n2oapp.security.admin.rest.api.ApplicationRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestApplicationCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;


/**
 * Реализация REST сервиса управления приложениями
 */
@Controller
public class ApplicationRestServiceImpl implements ApplicationRestService {
    @Autowired
    private ApplicationService service;

    @Override
    public Page<Application> findAll(RestApplicationCriteria criteria) {
        return service.findAll(criteria);
    }

    @Override
    public Application getById(String code) {
        return service.getById(code);
    }

    @Override
    public Application create(ApplicationForm systemForm) {
        return service.create(systemForm);
    }

    @Override
    public Application update(ApplicationForm systemForm) {
        return service.update(systemForm);

    }

    @Override
    public void delete(String code) {
        service.delete(code);
    }

}
