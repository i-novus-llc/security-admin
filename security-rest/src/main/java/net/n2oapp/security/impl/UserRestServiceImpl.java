package net.n2oapp.security.impl;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.rest.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

/**
 *Реализация сервиса управления пользоватлями
 */
@Controller
public class UserRestServiceImpl implements UserRestService {
    @Autowired
    private UserService service;


    @Override
    public Page<User> search(UserCriteria criteria) {

      return   service.findAll(criteria);
    }

    @Override
    public void create(User user) {
        service.create(user);
    }

    @Override
    public void update(Integer id) {
        //service.update(id);
    }

    @Override
    public void delete(Integer id) {
        service.delete(id);

    }
}
