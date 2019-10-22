package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.service.OrganizationService;
import net.n2oapp.security.admin.rest.api.OrganizationRestService;
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
        return client.getAll(criteria);
    }

}
