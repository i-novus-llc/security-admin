package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.bank.Bank;
import net.n2oapp.security.admin.api.model.bank.BankCreateForm;
import net.n2oapp.security.admin.api.model.bank.BankUpdateForm;
import net.n2oapp.security.admin.api.model.department.Department;
import net.n2oapp.security.admin.api.model.department.DepartmentUpdateForm;
import net.n2oapp.security.admin.api.model.department.DepartmentCreateForm;
import net.n2oapp.security.admin.api.service.BankService;
import net.n2oapp.security.admin.api.service.DepartmentService;
import net.n2oapp.security.admin.rest.api.BankRestService;
import net.n2oapp.security.admin.rest.api.DepartmentRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestBankCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestDepartmentCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;


/**
 * Реализация REST сервиса управления сведениями о банке
 */
@Controller
public class DepartmentRestServiceImpl implements DepartmentRestService {

    @Autowired
    private DepartmentService service;


    @Override
    public Page<Department> findAll(RestDepartmentCriteria criteria) {
        return service.findAll(criteria);
    }

    @Override
    public Department getById(String id) {
        return service.getById(id);
    }

    @Override
    public Department create(DepartmentCreateForm department) {
        return service.create(department);
    }

    @Override
    public Department update(DepartmentUpdateForm department) {
        return service.update(department);
    }
}
