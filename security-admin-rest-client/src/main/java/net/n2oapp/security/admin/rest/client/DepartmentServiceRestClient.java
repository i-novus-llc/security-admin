package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.DepartmentCriteria;
import net.n2oapp.security.admin.api.model.Department;
import net.n2oapp.security.admin.api.service.DepartmentService;
import net.n2oapp.security.admin.rest.api.DepartmentRestService;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления депаратаментами, для вызова rest
 */
public class DepartmentServiceRestClient implements DepartmentService {

    private DepartmentRestService client;

    public DepartmentServiceRestClient(DepartmentRestService client) {
        this.client = client;
    }

    @Override
    public Page<Department> findAll(DepartmentCriteria criteria) {
        return client.getAll(criteria);
    }

}
