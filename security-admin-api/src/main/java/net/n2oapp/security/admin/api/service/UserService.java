package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.model.User;

/**
 * Сервис управления пользователями
 */
public interface UserService {

    Integer create(User user);

    Integer update(User user);

    void delete(Integer id);
}
