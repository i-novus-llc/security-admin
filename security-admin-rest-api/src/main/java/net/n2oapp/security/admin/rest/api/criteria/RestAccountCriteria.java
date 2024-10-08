package net.n2oapp.security.admin.rest.api.criteria;

import io.swagger.annotations.ApiParam;
import jakarta.ws.rs.QueryParam;
import net.n2oapp.security.admin.api.criteria.AccountCriteria;
import org.springframework.data.domain.Sort;

import java.util.List;

public class RestAccountCriteria extends AccountCriteria {

    @QueryParam("userId")
    @Override
    @ApiParam(value = "Идентификатор пользователя")
    public void setUserId(Integer userId) {
        super.setUserId(userId);
    }

    @QueryParam("username")
    @Override
    @ApiParam(value = "Уникальное имя пользователя")
    public void setUsername(String username) {
        super.setUsername(username);
    }

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
}
