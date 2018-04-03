package net.n2oapp.security.admin.api.provider;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;

/**
 * Создание, изменение, удаление пользователя и ролей на sso сервере
 */
public interface SsoUserRoleProvider {

    /**
     * Создание пользователя
     * @param user
     * @return
     */
    String createUser(User user);

    /**
     * Удаление пользователя
     * @param userGuid
     */
    void deleteUser(String userGuid);


    /**
     * Создание роли
     * @param role
     * @return
     */
    void createRole(Role role);

    /**
     * Удаление роли
     * @param roleGuid
     */
    void deleteRole(String roleGuid);

}
