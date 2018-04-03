package net.n2oapp.security.admin.impl.provider;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import org.springframework.stereotype.Service;

/**
 * Дефолтная реализация
 */
public class SimpleSsoUserRoleProvider implements SsoUserRoleProvider {
    @Override
    public String createUser(User user) {
        return null;
    }

    @Override
    public void deleteUser(String userGuid) {
    }

    @Override
    public void createRole(Role role) {
    }

    @Override
    public void deleteRole(String roleGuid) {
    }
}
