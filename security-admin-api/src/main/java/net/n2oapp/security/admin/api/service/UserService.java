package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Сервис управления пользователями
 */

public interface UserService {

    Integer create(User user);

    Integer update(User user);

    void delete(Integer id);

    User getById (Integer id);

    Page<User> findAll (UserCriteria criteria);


}
