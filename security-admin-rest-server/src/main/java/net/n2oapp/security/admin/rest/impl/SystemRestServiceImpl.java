package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import net.n2oapp.security.admin.api.service.SystemService;
import net.n2oapp.security.admin.rest.api.SystemRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestSystemCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

/**
 * Реализация REST сервиса управления приложениями и системами
 */
@Controller
public class SystemRestServiceImpl implements SystemRestService {
    @Autowired
    private SystemService service;

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
