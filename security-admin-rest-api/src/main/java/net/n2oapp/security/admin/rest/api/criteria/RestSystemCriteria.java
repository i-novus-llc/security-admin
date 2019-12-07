package net.n2oapp.security.admin.rest.api.criteria;

import io.swagger.annotations.ApiParam;
import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import org.springframework.data.domain.Sort;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Модель фильтрации систем для rest вызовов
 */
public class RestSystemCriteria extends SystemCriteria {

    @QueryParam("page")
    @Override
    @ApiParam(value = "Номер страницы")
    public void setPage(int page) {
        super.setPage(page);
    }

    @QueryParam("size")
    @DefaultValue("10")
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
    @ApiParam(value = "Наименование системы")
    public void setName(String name) {
        super.setName(name);
    }

    @QueryParam("code")
    @Override
    @ApiParam(value = "Код системы")
    public void setCode(String code) {
        super.setCode(code);
    }

    @QueryParam("publicAccess")
    @Override
    @ApiParam(value = "Признак работы в режиме без авторизации")
    public void setPublicAccess(Boolean publicAccess) {
        super.setPublicAccess(publicAccess);
    }

    @QueryParam("codeList")
    @Override
    @ApiParam(value = "Список кодов систем")
    public void setCodeList(List<String> codeList) {
        super.setCodeList(codeList);
    }
}
