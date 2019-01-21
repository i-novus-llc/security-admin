package net.n2oapp.security.admin.rest.api.criteria;

import net.n2oapp.security.admin.api.criteria.EmployeeDomrfCriteria;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.UUID;

/**
 * Модель фильтрации уполномоченных лиц ДОМ.РФ для rest вызовов
 */
public class RestEmployeeDomrfCriteria extends EmployeeDomrfCriteria {
    @QueryParam("departmentId")
    @Override
    public void setDepartmentId(UUID departmentId) {
        super.setDepartmentId(departmentId);
    }

    @QueryParam("sort")
    @Override
    public void setOrders(List<Sort.Order> orders) {
        super.setOrders(orders);
    }
}
