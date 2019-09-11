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
    public Page<Application> findAllApplications(RestApplicationCriteria criteria) {
        return service.findAllApplications(criteria);
    }

    @Override
    public Application getApplication(String code) {
        return service.getApplication(code);
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
    public Page<AppSystem> findAllSystems(RestSystemCriteria criteria) {
        return service.findAllSystems(criteria);
    }

    @Override
    public AppSystem getSystem(String code) {
        return service.getSystem(code);
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
