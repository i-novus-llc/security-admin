/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.n2oapp.security.auth.common;

import net.n2oapp.security.admin.api.model.UserDetailsToken;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import net.n2oapp.security.auth.common.authority.SystemGrantedAuthority;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * Извлекает Userinfo из ответа Keycloak
 */
public class AuthoritiesPrincipalExtractor implements PrincipalExtractor, AuthoritiesExtractor {

    private static final String GRANTED_AUTHORITY_KEY = "GrantedAuthorityKey";

    private String[] PRINCIPAL_KEYS = new String[]{"username", "preferred_username", "login", "sub"};
    private static final String[] SURNAME_KEYS = new String[]{"surname", "second_name", "family_name", "lastName"};
    private static final String[] NAME_KEYS = new String[]{"first_name", "given_name", "name", "firstName"};
    private static final String[] PATRONYMIC_KEYS = new String[]{"middleName"};
    private static final String[] EMAIL_KEYS = new String[]{"email", "e-mail", "mail"};
    private static final String[] GUID_KEYS = new String[]{"sub", "oid"};
    private static final String[] AUTHORITIES_KEYS = new String[]{"roles", "authorities", "realm_access.roles", "resource_access.roles"};

    private final UserDetailsService userDetailsService;

    private final String externalSystem;

    public AuthoritiesPrincipalExtractor(UserDetailsService userDetailsService, String externalSystem) {
        this.userDetailsService = userDetailsService;
        this.externalSystem = externalSystem;
    }

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        net.n2oapp.security.admin.api.model.User model = getUser(map);
        if (model == null) {
            return null;
        }
        User user = new User(model.getUsername(), "N/A", getAuthorities(map, model), model.getSurname(), model.getName(),
                model.getPatronymic(), model.getEmail());
        if (nonNull(model.getDepartment())) {
            user.setDepartment(model.getDepartment().getCode());
            user.setDepartmentName(model.getDepartment().getName());
        }
        if (nonNull(model.getOrganization())) {
            user.setOrganization(model.getOrganization().getCode());
        }
        if (nonNull(model.getRegion())) {
            user.setRegion(model.getRegion().getCode());
        }
        if (nonNull(model.getUserLevel())) {
            user.setUserLevel(model.getUserLevel().toString());
        }
        return user;
    }

    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        return getAuthorities(map, null);
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
        String patronymic = extractFromMap(PATRONYMIC_KEYS, map) == null ? null : (String) extractFromMap(PATRONYMIC_KEYS, map);

        UserDetailsToken token = new UserDetailsToken();
        token.setUsername(username);
        token.setRoleNames(roleList);
        token.setExtUid((String) extractFromMap(GUID_KEYS, map));
        token.setName(name);
        token.setSurname(surname);
        token.setPatronymic(patronymic);
        token.setEmail(email);
        token.setExternalSystem(externalSystem);
        net.n2oapp.security.admin.api.model.User user = userDetailsService.loadUserDetails(token);
        map.put("system", externalSystem);

        return user;
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
        if (nonNull(user) && nonNull(user.getRoles())) {
            authorities.addAll(user.getRoles().stream().map(r -> new RoleGrantedAuthority(r.getCode())).collect(Collectors.toList()));
            authorities.addAll(user.getRoles().stream().filter(r -> nonNull(r.getPermissions())).flatMap(r -> r.getPermissions().stream())
                    .map(p -> new PermissionGrantedAuthority(p.getCode())).collect(Collectors.toList()));
            authorities.addAll(user.getRoles().stream().filter(role -> nonNull(role.getSystem())).
                    map(role -> new SystemGrantedAuthority(role.getSystem().getCode())).collect(Collectors.toList()));
            authorities.addAll(user.getRoles().stream().filter(r -> nonNull(r.getPermissions())).flatMap(r -> r.getPermissions().stream())
                    .filter(permission -> nonNull(permission.getSystem())).map(p -> new SystemGrantedAuthority(p.getSystem().getCode())).collect(Collectors.toList()));
            authorities = authorities.stream().distinct().collect(Collectors.toList());
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
