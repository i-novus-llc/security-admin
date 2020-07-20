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
package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.OrgCategoryCriteria;
import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.api.model.OrgCategory;
import net.n2oapp.security.admin.api.model.Organization;
import org.springframework.data.domain.Page;

/**
 * Сервис управления организациями
 */
public interface OrganizationService {

    /**
     * Найти все организации по критериям поиска
     *
     * @param criteria Критерии поиска
     * @return Страница найденных организаций
     */
    Page<Organization> findAll(OrganizationCriteria criteria);

    /**
     * Найти организацию по уникальному идентификатору записи
     *
     * @param id Уникальный идентификатор
     * @return Найденная организация
     */
    Organization find(Integer id);

    /**
     * Найти все категории организаций по критериям поиска
     *
     * @param criteria Критерии поиска
     * @return Страница категории организаций
     */
    Page<OrgCategory> findAllCategories(OrgCategoryCriteria criteria);

    /**
     * Создать организацию
     *
     * @param organization Форма организации
     * @return Созданная организация
     */
    Organization create(Organization organization);

    /**
     * Обновить организацию
     *
     * @param organization Форма организации
     * @return Обновленная организация
     */
    Organization update(Organization organization);

    /**
     * Удалить организацию
     *
     * @param id Уникальный идентификатор записи организации
     */
    void delete(Integer id);

}
