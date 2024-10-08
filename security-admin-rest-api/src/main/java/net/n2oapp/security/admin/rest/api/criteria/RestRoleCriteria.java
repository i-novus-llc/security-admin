/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.n2oapp.security.admin.rest.api.criteria;

import io.swagger.annotations.ApiParam;
import jakarta.ws.rs.QueryParam;
import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import org.springframework.data.domain.Sort;

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
    @ApiParam(value = "Список кодов прав доступа")
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

    @QueryParam("forForm")
    @Override
    public void setForForm(Boolean forForm) {
        super.setForForm(forForm);
    }

    @QueryParam("groupBySystem")
    @Override
    public void setGroupBySystem(Boolean groupBySystem) {
        super.setGroupBySystem(groupBySystem);
    }

    @QueryParam("orgRoles")
    @Override
    public void setOrgRoles(List<Integer> orgRoles) {
        super.setOrgRoles(orgRoles);
    }

    @QueryParam("filterByOrgRoles")
    @Override
    public void setFilterByOrgRoles(Boolean filterByOrgRoles) {
        super.setFilterByOrgRoles(filterByOrgRoles);
    }
}
