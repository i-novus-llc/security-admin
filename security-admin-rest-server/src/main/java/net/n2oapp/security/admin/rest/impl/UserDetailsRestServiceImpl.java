package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.rest.api.UserDetailsRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestUserDetailsToken;

public class UserDetailsRestServiceImpl implements UserDetailsRestService {

    private UserDetailsService userDetailsService;

    public UserDetailsRestServiceImpl(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public User loadDetails(RestUserDetailsToken token) {
        return userDetailsService.loadUserDetails(token);
    }
}
