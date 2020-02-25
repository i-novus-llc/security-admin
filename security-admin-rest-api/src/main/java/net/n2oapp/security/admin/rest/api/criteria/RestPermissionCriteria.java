package net.n2oapp.security.admin.rest.api.criteria;

import io.swagger.annotations.ApiParam;
import net.n2oapp.security.admin.api.criteria.PermissionCriteria;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Модель фильтрации прав доступа для rest вызовов
 */
public class RestPermissionCriteria extends PermissionCriteria {

    @QueryParam("name")
    @Override
    @ApiParam(value = "Название права доступа")
    public void setName(String name) {
        super.setName(name);
    }

    @QueryParam("code")
    @Override
    @ApiParam(value = "Код права доступа")
    public void setCode(String code) {
        super.setCode(code);
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

    @QueryParam("systemCode")
    @Override
    @ApiParam(value = "Код системы")
    public void setSystemCode(String systemCode) {
        super.setSystemCode(systemCode);
    }

    @QueryParam("userLevel")
    @Override
    @ApiParam(value = "Код системы")
    public void setUserLevel(String userLevel) {
        super.setUserLevel(userLevel);
    }

    @QueryParam("forForm")
    @Override
    public void setForForm(Boolean forForm) {
        super.setForForm(forForm);
    }

    @QueryParam("withSystem")
    @Override
    public void setWithSystem(Boolean withSystem) {
        super.setWithSystem(withSystem);
    }

    @QueryParam("withoutParent")
    @Override
    public void setWithoutParent(Boolean withoutParent) {
        super.setWithoutParent(withoutParent);
    }
}
