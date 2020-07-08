package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.OrgCategory;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.service.OrganizationService;
import net.n2oapp.security.admin.rest.api.OrganizationReadRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestOrgCategoryCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestOrganizationCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;


/**
 * Реализация REST сервиса для чтения организаций
 */
@Controller
public class OrganizationReadRestServiceImpl implements OrganizationReadRestService {

    @Autowired
    private OrganizationService service;

    @Override
    public Page<Organization> getAll(RestOrganizationCriteria criteria) {
        return service.findAll(criteria);
    }

    @Override
    public Organization get(Integer id) {
        return service.find(id);
    }

    @Override
    public Page<OrgCategory> getAllCategories(RestOrgCategoryCriteria criteria) {
        return service.findAllCategories(criteria);
    }
}
