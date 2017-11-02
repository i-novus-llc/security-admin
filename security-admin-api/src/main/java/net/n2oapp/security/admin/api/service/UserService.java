package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.Criteria;
import net.n2oapp.security.admin.api.model.User;
import org.springframework.data.domain.Page;

/**
 * Сервис управления пользователями
 */

public interface UserService {

    Integer create(User user);

    Integer update(User user);

    void delete(Integer id);

    User getById (Integer id);

  //  Page<User> findAll (Criteria criteria);


}
