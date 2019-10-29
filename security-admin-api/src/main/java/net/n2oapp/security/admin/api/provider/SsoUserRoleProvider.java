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
