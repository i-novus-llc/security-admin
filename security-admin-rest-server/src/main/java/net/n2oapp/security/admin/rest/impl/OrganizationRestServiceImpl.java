package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.service.OrganizationService;
import net.n2oapp.security.admin.rest.api.OrganizationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;


/**
 * Реализация REST сервиса управления организациями
 */
@Controller
public class OrganizationRestServiceImpl implements OrganizationRestService {
    @Autowired
    private OrganizationService service;

    @Override
    public Page<Organization> getAll(OrganizationCriteria criteria) {
        return service.findAll(criteria);
    }
}
