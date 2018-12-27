package net.n2oapp.security.admin.rest.api.criteria;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.UUID;

/**
 * Модель фильтрации пользователей для rest вызовов
 */
public class RestUserCriteria extends UserCriteria {
    @QueryParam("username")
    @Override
    public void setUsername(String username) {
        super.setUsername(username);
    }

    @QueryParam("fio")
    @Override
    public void setFio(String fio) {
        super.setFio(fio);
    }

    @QueryParam("bank")
    @Override
    public  void setBank(UUID bank) {
        super.setBank(bank);
    }

    @QueryParam("isActive")
    @Override
    public void setIsActive(Boolean isActive) {
        super.setIsActive(isActive);
    }

    @QueryParam("roles")
    @Override
    public void setRoleIds(List<Integer> roleIds) {
        super.setRoleIds(roleIds);
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
