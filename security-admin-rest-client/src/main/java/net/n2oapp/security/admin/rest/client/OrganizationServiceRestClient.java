package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.service.OrganizationService;
import net.n2oapp.security.admin.rest.api.OrganizationRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestOrganizationCriteria;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления организациями, для вызова rest
 */
public class OrganizationServiceRestClient implements OrganizationService {

    private OrganizationRestService client;

    public OrganizationServiceRestClient(OrganizationRestService client) {
        this.client = client;
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
        return client.getAll(restOrganizationCriteria);
    }

}
