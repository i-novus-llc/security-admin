package net.n2oapp.security.admin.service;

import net.n2oapp.security.admin.model.User;

/**
 * Сервис управления пользователями
 */
public interface UserService {

    Integer create(User user);

    Integer update(User user);

    void delete(Integer id);
}
