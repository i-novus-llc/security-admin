package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.model.Role;

/**
 *  Сервис управления ролями
 */
public interface RoleService {

    Integer create(Role role);

    Integer update(Role role);

    void delete(Integer id);
}