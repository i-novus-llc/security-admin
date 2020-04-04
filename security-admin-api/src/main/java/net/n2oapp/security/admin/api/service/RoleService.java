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

import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import org.springframework.data.domain.Page;

/**
 * Сервис управления ролями
 */
public interface RoleService {

    /**
     * Создать роль
     *
     * @param role Модель роли для создания
     * @return Созданная роль
     */
    Role create(RoleForm role);

    /**
     * Изменить роль
     *
     * @param role Модель роли для изменения
     * @return Измененная роль
     */
    Role update(RoleForm role);

    /**
     * Удалить роль
     *
     * @param id Идентификатор роли
     */
    void delete(Integer id);

    /**
     * Получить роль по идентификатору
     *
     * @param id Идентификатор роли
     * @return Модель роли
     */
    Role getById(Integer id);

    /**
     * Найти все роли по критериям поиска
     *
     * @param criteria Критерии поиска
     * @return Страница найденных ролей
     */
    Page<Role> findAll(RoleCriteria criteria);

    /**
     * Возвращает количество
     * пользователей с данной ролью
     *
     * @param roleId - идентификатор роли
     * @return Количество пользователей
     */
    Integer countUsersWithRole(Integer roleId);

}
