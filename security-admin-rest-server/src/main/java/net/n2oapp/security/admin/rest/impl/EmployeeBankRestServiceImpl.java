package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.EmployeeBank;
import net.n2oapp.security.admin.api.model.EmployeeBankForm;
import net.n2oapp.security.admin.api.service.EmployeeBankService;
import net.n2oapp.security.admin.rest.api.EmployeeBankRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestEmployeeBankCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

import java.util.UUID;


/**
 * Реализация REST сервиса управления уполномоченными лицами банка
 */
@Controller
public class EmployeeBankRestServiceImpl implements EmployeeBankRestService {

    @Autowired
    private EmployeeBankService service;

    @Override
    public Page<EmployeeBank> findByBank(RestEmployeeBankCriteria criteria) {
        return service.findByBank(criteria);
    }

    @Override
    public EmployeeBank create(EmployeeBankForm employeeBankForm) {
        return service.create(employeeBankForm);
    }

    @Override
    public EmployeeBank get(UUID employeeBankId) {
        return service.get(employeeBankId);
    }
}
