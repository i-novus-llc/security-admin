package net.n2oapp.security.auth.simple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Аутентификация пользователя
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final static String GET_USER_BY_USERNAME = "select id from sec.user  where username = :username and is_active";
    private final static String GET_USER_BY_USERNAME_AND_PASSWORD = "select u.id from sec.user u where u.username = :username and u.password = :password and is_active";

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


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

    private boolean isAuthenticate(String name, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", name);
        List<Integer> userId = jdbcTemplate.query(GET_USER_BY_USERNAME, params, (resultSet, i) -> {
            return resultSet.getInt("id");
        });
        if (userId == null || userId.size() != 1) {
            throw new UsernameNotFoundException(String.format("User with username %s not found!", name));
        }
        params.put("password", password);
        userId = jdbcTemplate.query(GET_USER_BY_USERNAME_AND_PASSWORD, params, (resultSet, i) -> {
            return resultSet.getInt("id");
        });
        if (userId == null || userId.size() != 1) {
            return false;
        }
        return true;
    }
}
