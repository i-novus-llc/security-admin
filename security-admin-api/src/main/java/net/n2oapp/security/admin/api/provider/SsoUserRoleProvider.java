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
package net.n2oapp.security.admin.api.provider;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.SsoUser;

/**
 * Создание, изменение, удаление пользователя и ролей на sso сервере
 */
public interface SsoUserRoleProvider {

    /**
     * Поддерживает ли провайдер синхронизацию пользователя
     * @param ssoName наименование sso
     */
    boolean isSupports(String ssoName);

    /**
     * Создание пользователя
     * @param user  пользователь для создания
     * @return  пользователь с обновленными данными
     */
    SsoUser createUser(SsoUser user);

    /**
     * Изменение пользователя
     * @param user  пользователь
     */
    void updateUser(SsoUser user);

    /**
     * Удаление пользователя
     * @param user
     */
    void deleteUser(SsoUser user);

    /**
     * Изменение активности пользователя
     * @param user
     */
    void changeActivity(SsoUser user);

    /**
     * Создание роли
     * @param role  Роль для создания
     * @return  роль с обновленными данными
     */
    Role createRole(Role role);

    /**
     * Редактирование роли
     * @param role  роль
     */
    void updateRole(Role role);

    /**
     * Удаление роли
     * @param role
     */
    void deleteRole(Role role);

    /**
     * Сброс пароля
     * @param user
     */
    void resetPassword(SsoUser user);

    /**
     * Запуск синхронизации пользователей
     */
    void startSynchronization();

}
