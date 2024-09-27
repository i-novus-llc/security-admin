package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.Password;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.model.UserRegisterForm;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * Реализация REST сервиса управления пользоватлями
 */
@Controller
public class UserRestServiceImpl implements UserRestService {

    private final UserService service;

    public UserRestServiceImpl(UserService service) {
        this.service = service;
    }

    @Override
    public Page<User> findAll(RestUserCriteria criteria) {
        return service.findAll(criteria);
    }

    @Override
    public User getById(int id) {
        return service.getById(id);
    }

    @Override
    public User create(UserForm user) {
        return service.create(user);
    }

    @Override
    public User register(UserRegisterForm user) {
        return service.register(user);
    }

    @Override
    public User update(UserForm user) {
        return service.update(user);
    }

    @Override
    public User patch(Map<String, Object> userForm) {
        return service.patch(userForm);
    }

    @Override
    public void delete(Integer id) {
        service.delete(id);
    }

    @Override
    public User changeActive(Integer id) {
        return service.changeActive(id);
    }

    @Override
    public User loadSimpleDetails(Integer id) {
        return service.loadSimpleDetails(id);
    }

    @Override
    public void resetPassword(UserForm user) {
        service.resetPassword(user);
    }

    @Override
    public Password generatePassword() {
        return service.generatePassword();
    }
}
