package net.n2oapp.security.auth.simple;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Аутентификация пользователя
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(authentication.getPrincipal() == null) {
            throw new UsernameNotFoundException("");
        }
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        if(isAuthenticate(name, password)) {
            return new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>());
        }
        throw new BadCredentialsException("Username or password is not valid!");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                UsernamePasswordAuthenticationToken.class);
    }

    private boolean isAuthenticate(String username, String password) {
        UserCriteria criteria = new UserCriteria();
        criteria.setUsername(username);
        Page<User> users = userService.findAll(criteria);
        if (users == null || users.getTotalElements() != 1) {
            throw new UsernameNotFoundException(String.format("User with username %s not found!", username));
        }
        criteria.setPassword(password);
        users = userService.findAll(criteria);
        if (users == null || users.getTotalElements() != 1) {
            return false;
        }
        return true;
    }
}
