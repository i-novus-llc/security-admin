package net.n2oapp.security.auth.common;

import net.n2oapp.security.admin.api.model.UserDetailsToken;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Создание объекта пользователя из информации в SSO сервере
 */
@Component
public class AuthoritiesPrincipalExtractor implements PrincipalExtractor, AuthoritiesExtractor {

    private static final String GRANTED_AUTHORITY_KEY = "GrantedAuthorityKey";

    private static String[] PRINCIPAL_KEYS = new String[]{"username", "preferred_username",
            "login", "sub"};
    private static final String[] SURNAME_KEYS = new String[]{"surname", "second_name", "family_name", "lastName"};
    private static final String[] NAME_KEYS = new String[]{"first_name", "given_name", "name", "firstName"};
    private static final String[] EMAIL_KEYS = new String[]{"email", "e-mail", "mail"};
    private static final String[] GUID_KEYS = new String[]{"sub", "oid"};
    private static final String[] AUTHORITIES_KEYS = new String[]{"roles", "authorities", "realm_access.roles", "resource_access.roles"};

    private UserDetailsService userDetailsService;

    private String authServer;

    public AuthoritiesPrincipalExtractor(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        net.n2oapp.security.admin.api.model.User user = getUser(map);
        if (user == null) {
            return null;
        }

        return new User(user.getUsername(), "N/A", getAuthorities(map, user), user.getSurname(), user.getName(),
                user.getPatronymic(), user.getEmail());
    }

    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        return getAuthorities(map, null);
    }

    public AuthoritiesPrincipalExtractor setAuthServer(String sso) {
        this.authServer = sso;
        return this;
    }

    public AuthoritiesPrincipalExtractor setPrincipalKeys(String... pKeys) {
        PRINCIPAL_KEYS = pKeys;
        return this;
    }

    @SuppressWarnings("unchecked")
    private net.n2oapp.security.admin.api.model.User getUser(Map<String, Object> map) {
        Object usernameObj = extractFromMap(PRINCIPAL_KEYS, map);
        if (usernameObj == null)
            return null;
        Object roles = extractFromMap(AUTHORITIES_KEYS, map);
        List<String> roleList = new ArrayList<>();
        if (roles instanceof Collection)
            roleList = new ArrayList<>((Collection<String>) roles);
        String username = (String) usernameObj;
        String surname = extractFromMap(SURNAME_KEYS, map) == null ? null : (String) extractFromMap(SURNAME_KEYS, map);
        String name = extractFromMap(NAME_KEYS, map) == null ? null : (String) extractFromMap(NAME_KEYS, map);
        String email = extractFromMap(EMAIL_KEYS, map) == null ? null : (String) extractFromMap(EMAIL_KEYS, map);

        UserDetailsToken token = new UserDetailsToken();
        token.setUsername(username);
        token.setRoleNames(roleList);
        token.setExtUid((String) extractFromMap(GUID_KEYS, map));
        token.setSurname(surname);
        token.setName(name);
        token.setEmail(email);
        token.setExtSys(authServer);

        return userDetailsService.loadUserDetails(token);
    }

    @SuppressWarnings("unchecked")
    private List<GrantedAuthority> getAuthorities(Map<String, Object> map, net.n2oapp.security.admin.api.model.User user) {
        if (map.containsKey(GRANTED_AUTHORITY_KEY)) {
            return (List<GrantedAuthority>) map.get(GRANTED_AUTHORITY_KEY);
        }
        if (user == null) {
            user = getUser(map);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user != null && user.getRoles() != null) {
            authorities.addAll(user.getRoles().stream().map(r -> new RoleGrantedAuthority(r.getCode())).collect(Collectors.toList()));
            authorities.addAll(user.getRoles().stream().filter(r -> r.getPermissions() != null).flatMap(r -> r.getPermissions().stream())
                    .map(p -> new PermissionGrantedAuthority(p.getCode())).collect(Collectors.toList()));

            map.put(GRANTED_AUTHORITY_KEY, authorities);
        }
        return authorities;
    }

    private Object extractFromMap(String[] keys, Map<String, Object> map) {
        for (String key : keys) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return null;
    }
}
