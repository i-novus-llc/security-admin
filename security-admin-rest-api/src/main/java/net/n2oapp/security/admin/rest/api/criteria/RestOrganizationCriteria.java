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
import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.util.List;

public class RestOrganizationCriteria extends OrganizationCriteria {
    @QueryParam("shortName")
    @Override
    @ApiParam(value = "Краткое наименование организации")
    public void setShortName(String shortName) {
        super.setShortName(shortName);
    }

    @QueryParam("name")
    @Override
    @ApiParam(value = "Полное или краткое наименование организации")
    public void setName(String name) {
        super.setName(name);
    }

    @QueryParam("ogrn")
    @Override
    @ApiParam(value = "ОГРН организации")
    public void setOgrn(String ogrn) {
        super.setOgrn(ogrn);
    }

    @QueryParam("systemCodes")
    @Override
    @ApiParam(value = "Коды систем")
    public void setSystemCodes(List<String> systemCodes) {
        super.setSystemCodes(systemCodes);
    }

    @QueryParam("inn")
    @Override
    @ApiParam(value = "ИНН организации")
    public void setInn(String inn) {
        super.setInn(inn);
    }

    @QueryParam("categoryCodes")
    @Override
    @ApiParam(value = "Коды категорий организаций")
    public void setCategoryCodes(List<String> categoryCodes) {
        super.setCategoryCodes(categoryCodes);
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
