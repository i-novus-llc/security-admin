package net.n2oapp.auth.gateway.oauth;

import net.n2oapp.security.auth.common.User;
import net.n2oapp.security.auth.common.UserParamsUtil;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import java.util.*;

public class UserTokenConverter implements UserAuthenticationConverter {

    static final String ROLES = "roles";
    static final String PERMISSIONS = "permissions";
    static final String NAME = "name";
    static final String EMAIL = "email";
    static final String SURNAME = "surname";
    static final String PATRONYMIC = "patronymic";
    static final String SID = "sid";

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
            if (authentication.getDetails() instanceof Map) {
                response.put(SID, ((Map) authentication.getDetails()).get(SID));
            } else if (authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
                response.put(SID, ((OAuth2AuthenticationDetails) authentication.getDetails()).getSessionId());
            }
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
            AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
            authentication.setDetails(map);
            return authentication;
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        return UserParamsUtil.extractRolesAndPermissions(map);
    }
}
