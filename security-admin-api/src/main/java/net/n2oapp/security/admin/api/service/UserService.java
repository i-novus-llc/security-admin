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

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.Password;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.model.UserRegisterForm;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * Сервис управления пользователями
 */
public interface UserService {

    /**
     * Создать пользователя
     *
     * @param user Модель пользователя
     * @return Созданный пользователь
     */
    User create(UserForm user);

    /**
     * Зарегистрировать пользователя
     *
     * @param user Модель пользователя
     * @return Зарегистрированный пользователь
     */
    User register(UserRegisterForm user);

    /**
     * Изменить пользователя
     *
     * @param user Модель пользователя
     * @return Измененный пользователь
     */
    User update(UserForm user);

    /**
     * Частичное обновление данных. Ожидает на вход мапу по своей структуре сходную
     * с моделью UserForm.
     *
     * @param userInfo Модель пользователя
     * @return Измененный пользователь
     * @see UserForm
     */
    User patch(Map<String, Object> userInfo);

    /**
     * Удалить пользователя
     *
     * @param id Идентификатор пользователя
     */
    void delete(Integer id);

    /**
     * Получить пользователя по идентификатору
     *
     * @param id Идентификатор
     * @return Модель пользователя
     */
    User getById(Integer id);

    /**
     * Найти всех пользователей по критериям поиска
     *
     * @param criteria Критерии поиска
     * @return Страница найденных пользователей
     */
    Page<User> findAll(UserCriteria criteria);

    /**
     * Изменить статус пользователя
     *
     * @param id Идентификатор
     * @return Модель пользователя
     */
    User changeActive(Integer id);

    /**
     * Проверить уникальность имени пользователя
     *
     * @param username имя пользователя
     * @return <code>true</code> имя уникально <code>false</code> иначе
     */
    Boolean checkUniqueUsername(String username);

    /**
     * Загрузить простейшую информацию о пользователе (имя, почта и временный пароль)
     *
     * @param id Идентификатор
     * @return Модель пользователя
     */
    User loadSimpleDetails(Integer id);

    /**
     * Сбросить пароль пользователя
     *
     * @param user Модель пользователя
     */
    void resetPassword(UserForm user);

    /**
     * Сгенерировать пароль согласно
     * установленной политике паролей
     *
     * @return Пароль
     */
    Password generatePassword();
}
