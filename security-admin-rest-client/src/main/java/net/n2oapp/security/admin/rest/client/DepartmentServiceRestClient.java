package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.DepartmentCriteria;
import net.n2oapp.security.admin.api.model.department.Department;
import net.n2oapp.security.admin.api.model.department.DepartmentCreateForm;
import net.n2oapp.security.admin.api.model.department.DepartmentUpdateForm;
import net.n2oapp.security.admin.api.service.DepartmentService;
import net.n2oapp.security.admin.rest.api.DepartmentRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestDepartmentCriteria;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления пользователями, для вызова rest
 */
public class DepartmentServiceRestClient implements DepartmentService {

    private DepartmentRestService client;

    public DepartmentServiceRestClient(DepartmentRestService client) {
        this.client = client;
    }

    @Override
    public Page<Department> findAll(DepartmentCriteria criteria) {
        RestDepartmentCriteria restCriteria = new RestDepartmentCriteria();
        restCriteria.setPage(criteria.getPageNumber());
        restCriteria.setSize(criteria.getPageSize());
        restCriteria.setName(criteria.getName());
        restCriteria.setOrders(criteria.getOrders());
        return client.findAll(restCriteria);
    }

    @Override
    public Department getById(String id) {
        return client.getById(id);
    }

    @Override
    public Department create(DepartmentCreateForm department) {
        return client.create(department);
    }

    @Override
    public Department update(DepartmentUpdateForm department) {
        return client.update(department);
    }
}
