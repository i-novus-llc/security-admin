package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.api.service.ApplicationSystemService;
import net.n2oapp.security.admin.rest.api.ApplicationSystemRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestApplicationCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestSystemCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;


/**
 * Реализация REST сервиса управления приложениями и системами
 */
@Controller
public class ApplicationSystemRestServiceImpl implements ApplicationSystemRestService {
    @Autowired
    private ApplicationSystemService service;

    @Override
    public Page<Application> findAllApplication(RestApplicationCriteria criteria) {
        return service.findAllApplication(criteria);
    }

    @Override
    public Application getApplicationById(String code) {
        return service.getApplicationById(code);
    }

    @Override
    public Application createApplication(Application systemForm) {
        return service.createApplication(systemForm);
    }

    @Override
    public Application updateApplication(Application systemForm) {
        return service.updateApplication(systemForm);

    }

    @Override
    public void deleteApplication(String code) {
        service.deleteApplication(code);
    }

    @Override
    public Page<AppSystem> findAllSystem(RestSystemCriteria criteria) {
        return service.findAllSystem(criteria);
    }

    @Override
    public AppSystem getSystemById(String code) {
        return service.getSystemById(code);
    }

    @Override
    public AppSystem createSystem(AppSystemForm systemForm) {
        return service.createSystem(systemForm);
    }

    @Override
    public AppSystem updateSystem(AppSystemForm systemForm) {
        return service.updateSystem(systemForm);

    }

    @Override
    public void deleteSystem(String code) {
        service.deleteSystem(code);
    }

}
