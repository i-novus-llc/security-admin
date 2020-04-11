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

import net.n2oapp.security.admin.api.criteria.PermissionCriteria;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.PermissionUpdateForm;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Сервис управления правами доступа
 */
public interface PermissionService {

    /**
     * Создать право доступа
     *
     * @param permission Модель права доступа
     * @return Созданное право доступа
     */
    Permission create(Permission permission);

    /**
     * Изменить имя права доступа
     *
     * @param permissionUpdateForm форма обновления права доступа
     * @return Измененное право доступа
     */
    Permission update(PermissionUpdateForm permissionUpdateForm);

    /**
     * Удалить право доступа
     *
     * @param code Идентификатор права доступа
     */
    void delete(String code);

    /**
     * Получить право доступа по идентификатору
     *
     * @param code Идентификатор
     * @return Модель прав доступа
     */
    Permission getByCode(String code);

    /**
     * Найти все права доступа по критериям поиска
     *
     * @param criteria Критерии поиска
     * @return Страница найденных всех прав доступа
     */
    Page<Permission> getAll(PermissionCriteria criteria);

    /**
     * Найти все права доступаии поиска по идентификатору родительского элемента
     *
     * @param parentCode Идентификатор
     * @return Страница найденных дочерних прав доступа
     */
    List<Permission> getAllByParentCode(String parentCode);
}
