package net.n2oapp.security.admin.service;

import net.n2oapp.security.admin.model.Role;

/**
 *
 */
public interface RoleService {

    Integer create(Role role);

    Integer update(Role role);

    void delete(Integer id);
}
