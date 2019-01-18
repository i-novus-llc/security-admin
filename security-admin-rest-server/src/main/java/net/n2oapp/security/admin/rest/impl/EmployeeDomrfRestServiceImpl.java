package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.EmployeeDomrf;
import net.n2oapp.security.admin.api.service.EmployeeDomrfService;
import net.n2oapp.security.admin.rest.api.EmployeeDomrfRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestEmployeeDomrfCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;


/**
 * Реализация REST сервиса управления уполномоченными лицами ДОМ.РФ
 */
@Controller
public class EmployeeDomrfRestServiceImpl implements EmployeeDomrfRestService {

    @Autowired
    private EmployeeDomrfService service;

    @Override
    public Page<EmployeeDomrf> findByDepartment(RestEmployeeDomrfCriteria criteria) {
        return service.findByDepartment(criteria);
    }
}
