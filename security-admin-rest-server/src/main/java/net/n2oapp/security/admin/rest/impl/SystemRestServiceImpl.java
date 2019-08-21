package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import net.n2oapp.security.admin.api.service.AppSystemService;
import net.n2oapp.security.admin.rest.api.SystemRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestSystemCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;


/**
 * Реализация REST сервиса управления системами
 */
@Controller
public class SystemRestServiceImpl implements SystemRestService {
    @Autowired
    private AppSystemService service;

    @Override
    public Page<AppSystem> findAll(RestSystemCriteria criteria) {
        return service.findAll(criteria);
    }

    @Override
    public AppSystem getById(String code) {
        return service.getById(code);
    }

    @Override
    public AppSystem create(AppSystemForm systemForm) {
        return service.create(systemForm);
    }

    @Override
    public AppSystem update(AppSystemForm systemForm) {
        return service.update(systemForm);

    }

    @Override
    public void delete(String code) {
        service.delete(code);
    }

}
