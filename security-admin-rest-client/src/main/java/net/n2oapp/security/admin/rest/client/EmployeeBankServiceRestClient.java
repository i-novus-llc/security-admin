package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.EmployeeBankCriteria;
import net.n2oapp.security.admin.api.model.EmployeeBank;
import net.n2oapp.security.admin.api.model.EmployeeBankForm;
import net.n2oapp.security.admin.api.service.EmployeeBankService;
import net.n2oapp.security.admin.rest.api.EmployeeBankRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestEmployeeBankCriteria;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления уполномоченными лицами банка, для вызова rest
 */
public class EmployeeBankServiceRestClient implements EmployeeBankService {

    private EmployeeBankRestService client;

    public EmployeeBankServiceRestClient(EmployeeBankRestService client) {
        this.client = client;
    }

    @Override
    public EmployeeBank create(EmployeeBankForm user) {
        return client.create(user);
    }

    @Override
    public Page<EmployeeBank> findByBank(EmployeeBankCriteria criteria) {
        RestEmployeeBankCriteria employeeBankCriteria = new RestEmployeeBankCriteria();
        employeeBankCriteria.setPage(criteria.getPageNumber());
        employeeBankCriteria.setSize(criteria.getPageSize());
        employeeBankCriteria.setBankId(criteria.getBankId());
        return client.findByBank(employeeBankCriteria);
    }

}
