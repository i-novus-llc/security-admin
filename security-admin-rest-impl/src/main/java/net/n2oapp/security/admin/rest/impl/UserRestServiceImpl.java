package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

import java.util.List;


/**
 * Реализация REST сервиса управления пользоватлями
 */
@Controller
public class UserRestServiceImpl implements UserRestService {
    @Autowired
    private UserService service;


    @Override
    public Page<User> search(Integer page, Integer size,
                             String username, String fio, Boolean isActive, List<Integer> roleIds) {
        UserCriteria criteria = new UserCriteria(page - 1, size);
        criteria.setUsername(username);
        criteria.setFio(fio);
        criteria.setIsActive(isActive);
        criteria.setRoleIds(roleIds);
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
