package net.n2oapp.security.rest.impl;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.rest.api.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

/**
 * Реализация REST сервиса управления пользоватлями
 */
@Controller
public class UserRestServiceImpl implements UserRestService {
    @Autowired
    private UserService service;


    @Override
    public Page<User> search(Integer page, Integer size,
                             String username, String name, String surname, String patronymic, Boolean isActive) {
        UserCriteria criteria = new UserCriteria(page - 1, size);
        criteria.setUsername(username);
        criteria.setName(name);
        criteria.setSurname(surname);
        criteria.setUsername(username);
        criteria.setPatronymic(patronymic);
        criteria.setIsActive(isActive);
        // criteria.setRoleIds();
        return service.findAll(criteria);
    }

    @Override
    public User getById(Integer id) {
        return service.getById(id);
    }

    @Override
    public User create(User user) {
        return service.create(user);
    }

    @Override
    public User update(User user) {
        return service.update(user);
    }

    @Override
    public void delete(Integer id) {
        service.delete(id);

    }
}
