package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.rest.api.UserDetailRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestUserDetailsToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserDetailRestServiceImpl implements UserDetailRestService {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public User loadDetails(RestUserDetailsToken token) {
        return userDetailsService.loadUserDetails(token);
    }
}
