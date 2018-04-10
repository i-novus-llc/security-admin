package net.n2oapp.security.admin.rest.api.criteria;

import net.n2oapp.security.admin.api.criteria.BaseCriteria;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Базовая модель фильтрации таблиц для rest вызова
 */
public class RestBaseCriteria extends BaseCriteria {
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
