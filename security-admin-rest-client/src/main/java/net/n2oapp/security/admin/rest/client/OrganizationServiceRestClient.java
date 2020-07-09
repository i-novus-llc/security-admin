package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.OrgCategoryCriteria;
import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.api.model.OrgCategory;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.service.OrganizationService;
import net.n2oapp.security.admin.rest.api.OrganizationPersistRestService;
import net.n2oapp.security.admin.rest.api.OrganizationRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestOrgCategoryCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestOrganizationCriteria;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления организациями, для вызова rest
 */
public class OrganizationServiceRestClient implements OrganizationService {

    private OrganizationRestService organizationRestClient;

    private OrganizationPersistRestService organizationPersistRestClient;

    public OrganizationServiceRestClient(OrganizationRestService organizationRestClient, OrganizationPersistRestService organizationPersistRestClient) {
        this.organizationRestClient = organizationRestClient;
        this.organizationPersistRestClient = organizationPersistRestClient;
    }

    @Override
    public Page<Organization> findAll(OrganizationCriteria criteria) {
        RestOrganizationCriteria restOrganizationCriteria = new RestOrganizationCriteria();
        restOrganizationCriteria.setShortName(criteria.getShortName());
        restOrganizationCriteria.setPage(criteria.getPage());
        restOrganizationCriteria.setOrders(criteria.getOrders());
        restOrganizationCriteria.setSize(criteria.getSize());
        restOrganizationCriteria.setName(criteria.getName());
        restOrganizationCriteria.setOgrn(criteria.getOgrn());
        restOrganizationCriteria.setSystemCodes(criteria.getSystemCodes());
        restOrganizationCriteria.setInn(criteria.getInn());
        restOrganizationCriteria.setCategoryCodes(criteria.getCategoryCodes());
        return organizationRestClient.getAll(restOrganizationCriteria);
    }

    @Override
    public Organization find(Integer id) {
        return organizationRestClient.get(id);
    }

    @Override
    public Page<OrgCategory> findAllCategories(OrgCategoryCriteria criteria) {
        RestOrgCategoryCriteria restCriteria = new RestOrgCategoryCriteria();
        restCriteria.setName(criteria.getName());
        restCriteria.setSize(criteria.getSize());
        restCriteria.setPage(criteria.getPage());
        restCriteria.setOrders(criteria.getOrders());
        return organizationRestClient.getAllCategories(restCriteria);
    }

    @Override
    public Organization create(Organization organization) {
        if (organizationPersistRestClient == null)
            throw new UnsupportedOperationException();
        return organizationPersistRestClient.create(organization);
    }

    @Override
    public Organization update(Organization organization) {
        if (organizationPersistRestClient == null)
            throw new UnsupportedOperationException();
        return organizationPersistRestClient.update(organization);
    }

    @Override
    public void delete(Integer id) {
        if (organizationPersistRestClient == null)
            throw new UnsupportedOperationException();
        organizationPersistRestClient.delete(id);
    }
}
