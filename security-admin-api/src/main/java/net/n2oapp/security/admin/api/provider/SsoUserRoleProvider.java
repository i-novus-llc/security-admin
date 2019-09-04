package net.n2oapp.security.admin.api.provider;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;

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
    User createUser(User user);

    /**
     * Изменение пользователя
     * @param user  пользователь
     */
    void updateUser(User user);

    /**
     * Удаление пользователя
     * @param user
     */
    void deleteUser(User user);

    /**
     * Изменение активности пользователя
     * @param user
     */
    void changeActivity(User user);

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

}
