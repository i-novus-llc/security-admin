package net.n2oapp.security.admin.impl.provider;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.SsoUser;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;

/**
 * Дефолтная реализация
 */
public class SimpleSsoUserRoleProvider implements SsoUserRoleProvider {

    @Override
    public boolean isSupports(String ssoName) {
        return true;
    }

    @Override
    public SsoUser createUser(SsoUser user) {
        return user;
    }

    @Override
    public void updateUser(SsoUser user) {
        // Дефолтная реализация без внешней SSO: синхронизация пользователя не требуется
    }

    @Override
    public void deleteUser(SsoUser user) {
        // Дефолтная реализация без внешней SSO: синхронизация пользователя не требуется
    }

    @Override
    public void changeActivity(SsoUser user) {
        // Дефолтная реализация без внешней SSO: синхронизация пользователя не требуется
    }

    @Override
    public Role createRole(Role role) {
        return role;
    }

    @Override
    public void updateRole(Role role) {
        // Дефолтная реализация без внешней SSO: синхронизация роли не требуется
    }

    @Override
    public void deleteRole(Role role) {
        // Дефолтная реализация без внешней SSO: синхронизация роли не требуется
    }

    @Override
    public void resetPassword(SsoUser user) {
        // Дефолтная реализация без внешней SSO: сброс пароля не требуется
    }
}
