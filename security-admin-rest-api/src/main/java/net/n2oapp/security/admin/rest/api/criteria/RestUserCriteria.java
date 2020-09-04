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
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;
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

    @QueryParam("email")
    @Override
    @ApiParam(value = "Email пользователя")
    public void setEmail(String email) {
        super.setEmail(email);
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

    @QueryParam("systems")
    @Override
    @ApiParam(value = "Список кодов систем")
    public void setSystems(List<String> systems) {
        super.setSystems(systems);
    }

    @QueryParam("roles")
    @Override
    @ApiParam(value = "Список идентификаторов ролей")
    public void setRoleIds(List<Integer> roleIds) {
        super.setRoleIds(roleIds);
    }

    @QueryParam("roleCodes")
    @Override
    @ApiParam(value = "Список кодов ролей")
    public void setRoleCodes(List<String> roleCodes) {
        super.setRoleCodes(roleCodes);
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

    @QueryParam("regionId")
    @Override
    @ApiParam(value = "id региона")
    public void setRegionId(Integer regionId) {
        super.setRegionId(regionId);
    }

    @QueryParam("organizations")
    @Override
    @ApiParam(value = "Список идентификаторов организаций")
    public void setOrganizations(List<Integer> organizations) {
        super.setOrganizations(organizations);
    }

    @QueryParam("departmentId")
    @Override
    @ApiParam(value = "id департамента")
    public void setDepartmentId(Integer departmentId) {
        super.setDepartmentId(departmentId);
    }

    @QueryParam("userLevel")
    @Override
    @ApiParam(value = "Уровень пользователя")
    public void setUserLevel(String userLevel) {
        super.setUserLevel(userLevel);
    }

    @QueryParam("lastActionDate")
    @Override
    @ApiParam(value = "Время последних изменений")
    public void setLastActionDate(LocalDateTime lastActionDate) {
        super.setLastActionDate(lastActionDate);
    }
}
