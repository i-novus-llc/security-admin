package net.n2oapp.security.admin.rest.api.criteria;

import io.swagger.annotations.ApiParam;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import org.springframework.data.domain.Sort;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Модель фильтрации пользователей для rest вызовов
 */
public class RestUserCriteria extends UserCriteria {
    @QueryParam("username")
    @Override
    @ApiParam(value = "Имя пользователя")
    public void setUsername(String username) {
        super.setUsername(username);
    }

    @QueryParam("fio")
    @Override
    @ApiParam(value = "ФИО")
    public void setFio(String fio) {
        super.setFio(fio);
    }

    @QueryParam("isActive")
    @Override
    @ApiParam(value = "Активный ли пользователь")
    public void setIsActive(String isActive) {
        super.setIsActive(isActive);
    }

    @QueryParam("roles")
    @Override
    @ApiParam(value = "Список идентификаторов ролей")
    public void setRoleIds(List<Integer> roleIds) {
        super.setRoleIds(roleIds);
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
