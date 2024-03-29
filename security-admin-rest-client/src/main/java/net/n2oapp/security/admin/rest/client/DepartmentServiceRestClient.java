package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.DepartmentCriteria;
import net.n2oapp.security.admin.api.model.Department;
import net.n2oapp.security.admin.api.service.DepartmentService;
import net.n2oapp.security.admin.rest.api.criteria.RestDepartmentCriteria;
import net.n2oapp.security.admin.rest.client.feign.DepartmentServiceFeignClient;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления депаратаментами, для вызова rest
 */
public class DepartmentServiceRestClient implements DepartmentService {

    private final DepartmentServiceFeignClient client;

    public DepartmentServiceRestClient(DepartmentServiceFeignClient client) {
        this.client = client;
    }

    @Override
    public Page<Department> findAll(DepartmentCriteria criteria) {
        RestDepartmentCriteria restDepartmentCriteria = new RestDepartmentCriteria();
        restDepartmentCriteria.setName(criteria.getName());
        restDepartmentCriteria.setPage(criteria.getPage());
        restDepartmentCriteria.setOrders(criteria.getOrders());
        restDepartmentCriteria.setSize(criteria.getSize());
        return client.findAll(restDepartmentCriteria);
    }

}
