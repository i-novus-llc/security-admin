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

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasLength;

/**
 * Пользователь с расширенными атрибутами
 */
@Getter
@Setter
public class OauthUser extends DefaultOidcUser implements UserDetails {
    private static final String DEFAULT_ROLE = "ROLE_USER";

    private String surname;
    private String firstName;
    private String patronymic;
    private String email;
    private String organization;
    private String region;
    private String department;
    private String departmentName;
    private String userLevel;
    private String accountId;
    private String username;

    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    @Override
    public String getName() {
        return username;
    }

    public OauthUser(String username, OidcIdToken idToken) {
        this(username, Collections.singleton(new RoleGrantedAuthority(DEFAULT_ROLE)), idToken);
    }

    public OauthUser(String username, Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken) {
        this(username, authorities, idToken, null);
    }

    public OauthUser(OauthUser oauthUser, Collection<? extends GrantedAuthority> authorities) {
        this(oauthUser.getName(), authorities, oauthUser.getIdToken(), oauthUser.getUserInfo());
        this.surname = oauthUser.surname;
        this.firstName = oauthUser.firstName;
        this.patronymic = oauthUser.patronymic;
        this.email = oauthUser.email;
        this.organization = oauthUser.organization;
        this.region = oauthUser.region;
        this.department = oauthUser.department;
        this.departmentName = oauthUser.departmentName;
        this.userLevel = oauthUser.userLevel;
        this.accountId = oauthUser.accountId;
    }

    public OauthUser(String username, Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, OidcUserInfo userInfo) {
        super(authorities, idToken, userInfo);
        this.username = username;
    }

    /**
     * Получение имени пользователя в формате: Фамиилия Имя Отчество
     */
    public String getUserFullName() {
        List<String> fullNameParts = new LinkedList<>();
        if (hasLength(surname))
            fullNameParts.add(surname);
        if (hasLength(firstName))
            fullNameParts.add(firstName);
        if (hasLength(patronymic))
            fullNameParts.add(patronymic);
        if (fullNameParts.isEmpty())
            fullNameParts.add(getUsername());
        return String.join(" ", fullNameParts);
    }

    /**
     * Получение имени пользователя в формате: Фамиилия И.О.
     */
    public String getUserShortName() {
        StringBuilder sb = new StringBuilder();
        if (hasLength(surname)) {
            sb.append(getSurname().trim()).append(' ');
            if (hasLength(firstName))
                sb.append(firstName.trim().toUpperCase().charAt(0)).append('.');
            if (hasLength(patronymic))
                sb.append(patronymic.trim().toUpperCase().charAt(0)).append('.');
        } else if (hasLength(firstName))
            sb.append(getName().trim());
        else
            sb.append(getUsername()).append(' ');
        return sb.toString();
    }

    /**
     * Получение имени пользователя в формате: Имя Фамилия
     */
    public String getUserNameSurname() {
        List<String> parts = new LinkedList<>();
        if (hasLength(firstName))
            parts.add(firstName);
        if (hasLength(surname))
            parts.add(surname);
        if (parts.isEmpty())
            parts.add(getUsername());
        return String.join(" ", parts);
    }

    public List<String> getRoles() {
        return getAuthorities()
                .stream()
                .filter(RoleGrantedAuthority.class::isInstance)
                .map(a -> ((RoleGrantedAuthority) a).getRole()).collect(Collectors.toList());
    }

    public List<String> getPermissions() {
        return getAuthorities()
                .stream()
                .filter(PermissionGrantedAuthority.class::isInstance)
                .map(p -> ((PermissionGrantedAuthority) p).getPermission()).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }
}
