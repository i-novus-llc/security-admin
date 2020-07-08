package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.OrgCategoryCriteria;
import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.api.model.OrgCategory;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.service.OrganizationService;
import net.n2oapp.security.admin.rest.api.OrganizationCUDRestService;
import net.n2oapp.security.admin.rest.api.OrganizationReadRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestOrgCategoryCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestOrganizationCriteria;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления организациями, для вызова rest
 */
public class OrganizationServiceRestClient implements OrganizationService {

    private OrganizationReadRestService readClient;

    private OrganizationCUDRestService cudClient;

    public OrganizationServiceRestClient(OrganizationReadRestService readClient, OrganizationCUDRestService cudClient) {
        this.readClient = readClient;
        this.cudClient = cudClient;
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
        return readClient.getAll(restOrganizationCriteria);
    }

    @Override
    public Organization find(Integer id) {
        return readClient.get(id);
    }

    @Override
    public Page<OrgCategory> findAllCategories(OrgCategoryCriteria criteria) {
        RestOrgCategoryCriteria restCriteria = new RestOrgCategoryCriteria();
        restCriteria.setName(criteria.getName());
        restCriteria.setSize(criteria.getSize());
        restCriteria.setPage(criteria.getPage());
        restCriteria.setOrders(criteria.getOrders());
        return readClient.getAllCategories(restCriteria);
    }

    @Override
    public Organization create(Organization organization) {
        if (cudClient == null)
            throw new UnsupportedOperationException();
        return cudClient.create(organization);
    }

    @Override
    public Organization update(Organization organization) {
        if (cudClient == null)
            throw new UnsupportedOperationException();
        return cudClient.update(organization);
    }

    @Override
    public void delete(Integer id) {
        if (cudClient == null)
            throw new UnsupportedOperationException();
        cudClient.delete(id);
    }
}
