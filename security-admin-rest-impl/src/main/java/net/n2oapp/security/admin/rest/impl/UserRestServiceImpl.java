package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserDetailsToken;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;

import java.util.List;


/**
 * Реализация REST сервиса управления пользоватлями
 */
@Controller
public class UserRestServiceImpl implements UserRestService {
    @Autowired
    private UserService service;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Page<User> search(Integer page, Integer size,
                             String username, String fio, Boolean isActive, List<Integer> roles) {
        UserCriteria criteria = new UserCriteria(page - 1, size, Sort.Direction.DESC, "id");
        criteria.setUsername(username);
        criteria.setFio(fio);
        criteria.setIsActive(isActive);
        criteria.setRoleIds(roles);
        return service.findAll(criteria);
    }

    @Override
    public User getById(Integer id) {
        return service.getById(id);
    }

    @Override
    public User create(UserForm user) {
        return service.create(user);
    }

    @Override
    public User update(UserForm user) {
        return service.update(user);
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
    public User loadDetails(String username, List<String> roleNames) {
        UserDetailsToken token = new UserDetailsToken();
        token.setUsername(username);
        token.setRoleNames(roleNames);
        return userDetailsService.loadUserDetails(token);
    }
}
