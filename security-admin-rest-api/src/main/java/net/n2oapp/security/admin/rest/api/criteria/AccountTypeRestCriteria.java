package net.n2oapp.security.admin.rest.api.criteria;

import io.swagger.annotations.ApiParam;
import jakarta.ws.rs.QueryParam;
import net.n2oapp.security.admin.api.criteria.AccountTypeCriteria;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Критерий фильтрации типов аккаунтов для rest вызовов
 */
public class AccountTypeRestCriteria extends AccountTypeCriteria {
    @QueryParam("page")
    @Override
    @ApiParam(value = "Номер страницы")
    public void setPage(int page) {
        super.setPage(page);
    }

    @QueryParam("size")
    @Override
    @ApiParam(value = "Количество записей на странице")
    public void setSize(int size) {
        super.setSize(size);
    }

    @QueryParam("sort")
    @Override
    @ApiParam(value = "Сортировка(массив из объектов с атрибутами direction и property)")
    public void setOrders(List<Sort.Order> orders) {
        super.setOrders(orders);
    }

    @QueryParam("name")
    @Override
    @ApiParam(value = "Наименование типа аккаунта")
    public void setName(String name) {
        super.setName(name);
    }

    @QueryParam("userLevel")
    @Override
    @ApiParam(value = "Уровень пользователя")
    public void setUserLevel(String userLevel) {
        super.setUserLevel(userLevel);
    }
}
