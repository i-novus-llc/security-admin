package net.n2oapp.security.admin.rest.api.criteria;

import net.n2oapp.security.admin.api.criteria.EmployeeBankCriteria;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.UUID;

/**
 * Модель фильтрации уполномоченных лиц по банку для rest вызовов
 */
public class RestEmployeeBankCriteria extends EmployeeBankCriteria {
    @QueryParam("bankId")
    @Override
    public void setBankId(UUID bankId) {
        super.setBankId(bankId);
    }

    @QueryParam("sort")
    @Override
    public void setOrders(List<Sort.Order> orders) {
        super.setOrders(orders);
    }
}
