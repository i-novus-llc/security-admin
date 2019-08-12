package net.n2oapp.security.admin;

import net.n2oapp.security.auth.common.User;
import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import java.util.*;

public class UserTokenConverter extends DefaultUserAuthenticationConverter {

    static final String ROLES = "roles";
    static final String PERMISSIONS = "permissions";
    static final String NAME = "name";
    static final String EMAIL = "email";
    static final String SURNAME = "surname";
    static final String PATRONYMIC = "patronymic";

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<>();
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            response.put(USERNAME, user.getUsername());
            response.put(NAME, user.getName());
            response.put(SURNAME, user.getSurname());
            response.put(PATRONYMIC, user.getPatronymic());
            response.put(EMAIL, user.getEmail());
        } else {
            response.put(USERNAME, authentication.getName());
        }
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            List<String> roles = new ArrayList<>();
            List<String> permissions = new ArrayList<>();
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority instanceof RoleGrantedAuthority)
                    roles.add(((RoleGrantedAuthority) authority).getRole());
                else
                    permissions.add(authority.getAuthority());
            }
            if (!roles.isEmpty())
                response.put(ROLES, roles);
            if (!permissions.isEmpty())
                response.put(PERMISSIONS, permissions);
        }
        return response;
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(USERNAME)) {
            Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
            Object principal = new User((String) map.get(USERNAME), "N/A", authorities, (String) map.get(SURNAME), (String) map.get(NAME),
                    (String) map.get(PATRONYMIC), (String) map.get(EMAIL));
            return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (map.containsKey(ROLES)) {
            for (String role : (List<String>) map.get(ROLES)) {
                authorities.add(new RoleGrantedAuthority(role));
            }
        }
        if (map.containsKey(PERMISSIONS)) {
            for (String role : (List<String>) map.get(PERMISSIONS)) {
                authorities.add(new PermissionGrantedAuthority(role));
            }
        }
        return authorities;
    }
}
