package net.n2oapp.security.admin.impl.provider;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
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
    public User createUser(User user) {
        return user;
    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public void changeActivity(User user) {

    }

    @Override
    public Role createRole(Role role) {
        return role;
    }

    @Override
    public void updateRole(Role role) {

    }

    @Override
    public void deleteRole(Role role) {

    }

    @Override
    public void startSynchronization() {

    }
}
