package net.n2oapp.security.admin.rest.api.criteria;

import net.n2oapp.security.admin.api.criteria.DepartmentCriteria;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Модель фильтрации подразделений для rest вызовов
 */
public class RestDepartmentCriteria extends DepartmentCriteria {
    @QueryParam("name")
    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @QueryParam("page")
    @Override
    public void setPage(int page) {
        super.setPage(page);
    }

    @QueryParam("size")
    @Override
    public void setSize(int size) {
        super.setSize(size);
    }

    @QueryParam("sort")
    @Override
    public void setOrders(List<Sort.Order> orders) {
        super.setOrders(orders);
    }
}
