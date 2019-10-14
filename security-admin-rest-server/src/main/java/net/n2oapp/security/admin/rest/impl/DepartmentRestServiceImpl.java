package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.criteria.DepartmentCriteria;
import net.n2oapp.security.admin.api.model.Department;
import net.n2oapp.security.admin.api.service.DepartmentService;
import net.n2oapp.security.admin.rest.api.DepartmentRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;


/**
 * Реализация REST сервиса управления департаментами
 */
@Controller
public class DepartmentRestServiceImpl implements DepartmentRestService {
    @Autowired
    private DepartmentService service;

    @Override
    public Page<Department> getAll(DepartmentCriteria criteria) {
        return service.findAll(criteria);
    }
}
