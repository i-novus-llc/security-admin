package net.n2oapp.security.admin.rest.api.criteria;

import net.n2oapp.security.admin.api.criteria.BankCriteria;
import net.n2oapp.security.admin.api.enumeration.BankTypeEnum;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.UUID;

/**
 * Модель фильтрации банков для rest вызовов
 */
public class RestBankCriteria extends BankCriteria {
    @QueryParam("name")
    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @QueryParam("type")
    @Override
    public void setType(BankTypeEnum type) {
        super.setType(type);
    }

    @QueryParam("parent")
    @Override
    public  void setParent(UUID parent) {
        super.setParent(parent);
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
