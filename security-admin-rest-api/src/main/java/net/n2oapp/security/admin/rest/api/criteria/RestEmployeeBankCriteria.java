package net.n2oapp.security.admin.rest.api.criteria;

import net.n2oapp.security.admin.api.criteria.EmployeeBankCriteria;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Модель фильтрации уполномоченных лиц по банку для rest вызовов
 */
public class RestEmployeeBankCriteria extends EmployeeBankCriteria {
    @QueryParam("bankId")
    @Override
    public void setBankId(String bankId) {
        super.setBankId(bankId);
    }

    @QueryParam("page")
    @Override
    public void setPage(int page) {
        //todo  у n2o отсчет начинается с 1
        super.setPage(page - 1);
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
