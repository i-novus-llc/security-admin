package net.n2oapp.security.admin.service;

import net.n2oapp.security.admin.model.User;

/**
 *
 */
public interface UserService {

    Integer create(User role);

    Integer update(User role);

    void delete(Integer id);
}
