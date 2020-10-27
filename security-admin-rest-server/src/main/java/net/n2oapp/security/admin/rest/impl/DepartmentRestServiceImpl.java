package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.Department;
import net.n2oapp.security.admin.api.service.DepartmentService;
import net.n2oapp.security.admin.rest.api.DepartmentRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestDepartmentCriteria;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;


/**
 * Реализация REST сервиса управления департаментами
 */
@Controller
public class DepartmentRestServiceImpl implements DepartmentRestService {
    private final DepartmentService service;

    public DepartmentRestServiceImpl(DepartmentService service) {
        this.service = service;
    }

    @Override
    public Page<Department> findAll(RestDepartmentCriteria criteria) {
        return service.findAll(criteria);
    }
}
