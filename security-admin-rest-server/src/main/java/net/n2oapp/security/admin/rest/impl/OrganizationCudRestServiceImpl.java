package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.service.OrganizationService;
import net.n2oapp.security.admin.rest.api.OrganizationCUDRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;

/**
 * Реализация REST сервиса управления организациями
 */
@Controller
@ConditionalOnProperty(name = "access.nsiOrganizationSync", havingValue = "false")
public class OrganizationCudRestServiceImpl implements OrganizationCUDRestService {

    @Autowired
    private OrganizationService service;

    @Override
    public Organization create(Organization organization) {
        return service.create(organization);
    }

    @Override
    public Organization update(Organization organization) {
        return service.update(organization);
    }

    @Override
    public void delete(Integer id) {
        service.delete(id);
    }
}
