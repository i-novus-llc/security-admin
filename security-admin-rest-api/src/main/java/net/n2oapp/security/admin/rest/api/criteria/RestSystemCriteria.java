package net.n2oapp.security.admin.rest.api.criteria;

import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Модель фильтрации систем для rest вызовов
 */
public class RestSystemCriteria extends SystemCriteria {

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

    @QueryParam("name")
    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @QueryParam("code")
    @Override
    public void setCode(String code) {
        super.setCode(code);
    }

}
