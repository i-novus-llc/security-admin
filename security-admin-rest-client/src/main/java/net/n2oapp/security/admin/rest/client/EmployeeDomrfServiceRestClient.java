package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.EmployeeDomrfCriteria;
import net.n2oapp.security.admin.api.model.EmployeeDomrf;
import net.n2oapp.security.admin.api.service.EmployeeDomrfService;
import net.n2oapp.security.admin.rest.api.EmployeeDomrfRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestEmployeeDomrfCriteria;
import org.springframework.data.domain.Page;

import java.util.UUID;

/**
 * Прокси реализация сервиса управления уполномоченными лицами ДОМ.РФ, для вызова rest
 */
public class EmployeeDomrfServiceRestClient implements EmployeeDomrfService {

    private EmployeeDomrfRestService client;

    public EmployeeDomrfServiceRestClient(EmployeeDomrfRestService client) {
        this.client = client;
    }

    @Override
    public Page<EmployeeDomrf> findByDepartment(EmployeeDomrfCriteria criteria) {
        RestEmployeeDomrfCriteria employeeDomrfCriteria = new RestEmployeeDomrfCriteria();
        employeeDomrfCriteria.setPage(criteria.getPageNumber());
        employeeDomrfCriteria.setSize(criteria.getPageSize());
        employeeDomrfCriteria.setDepartmentId(criteria.getDepartmentId());
        return client.findByDepartment(employeeDomrfCriteria);
    }

    @Override
    public EmployeeDomrf get(UUID id) {
        return client.get(id);
    }
}
