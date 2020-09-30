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

import net.n2oapp.security.admin.api.criteria.AccountTypeCriteria;
import net.n2oapp.security.admin.api.model.AccountType;
import org.springframework.data.domain.Page;

/**
 * Сервис типами аккаунтов
 */
public interface AccountTypeService {
    /**
     * Найти все типы аккаунтов по критериям поиска
     *
     * @param criteria Критерии поиска
     * @return Страница найденных типов аккаунтов
     */
    Page<AccountType> findAll(AccountTypeCriteria criteria);

    /**
     * Найти тип аккаунта по идентификатору
     *
     * @param id Идентификатор
     * @return Тип аккаунта
     */
    AccountType findById(Integer id);

    /**
     * Создать тип аккаунта
     *
     * @param accountType Модель типа аккаунта для создания
     * @return Созданный тип аккаунта
     */
    AccountType create(AccountType accountType);

    /**
     * Изменить тип аккаунта
     *
     * @param accountType Модель типа аккаунта для изменения
     * @return Измененный тип аккаунта
     */
    AccountType update(AccountType accountType);

    /**
     * Удалить тип аккаунта
     *
     * @param id Идентификатор типа аккаунта
     */
    void delete(Integer id);
}
