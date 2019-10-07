package net.n2oapp.security.admin.rest.api.criteria;

import io.swagger.annotations.ApiParam;
import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Модель фильтрации ролей для rest вызовов
 */
public class RestRoleCriteria extends RoleCriteria {

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
    @ApiParam(value = "Наименование роли")
    public void setName(String name) {
        super.setName(name);
    }

    @QueryParam("description")
    @Override
    @ApiParam(value = "Описание роли")
    public void setDescription(String description) {
        super.setDescription(description);
    }

    @QueryParam("permissions")
    @Override
    @ApiParam(value = "Список кодов привелегий")
    public void setPermissionCodes(List<String> permissionCodes) {
        super.setPermissionCodes(permissionCodes);
    }

    @QueryParam("system")
    @Override
    @ApiParam(value = "Список кодов систем")
    public void setSystemCodes(List<String> systemCodes) {
        super.setSystemCodes(systemCodes);
    }

    @QueryParam("userLevel")
    @Override
    @ApiParam(value = "Уровень пользователя")
    public void setUserLevel(String userLevel) {
        super.setUserLevel(userLevel);
    }
}
