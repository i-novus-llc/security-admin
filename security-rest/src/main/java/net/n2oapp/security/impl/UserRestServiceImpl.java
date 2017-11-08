package net.n2oapp.security.impl;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.rest.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

/**
 * Created by otihonova on 03.11.2017.
 */
@Controller
public class UserRestServiceImpl implements UserRestService {
    @Autowired
    private UserService service;


    @Override
    public Page<User> search(UserCriteria criteria) {
      return   service.findAll(criteria);
    }
}
