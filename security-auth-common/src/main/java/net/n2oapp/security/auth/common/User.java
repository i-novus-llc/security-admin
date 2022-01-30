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
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Пользователь с расширенными атрибутами
 */
@Getter
@Setter
public class User extends org.springframework.security.core.userdetails.User {
    private static final String DEFAULT_ROLE = "ROLE_USER";

    private String surname;
    private String name;
    private String patronymic;
    private String email;
    private String organization;
    private String region;
    private String department;
    private String departmentName;
    private String userLevel;
    private Integer accountId;

    public User(String username) {
        super(username, "", Collections.singleton(new RoleGrantedAuthority(DEFAULT_ROLE)));
    }

    public User(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public User(String username, String password, boolean enabled, boolean accountNonExpired,
                boolean credentialsNonExpired, boolean accountNonLocked,
                Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public User(String username, String password, Collection<? extends GrantedAuthority> authorities, String surname,
                String name, String patronymic, String email) {
        super(username, password, authorities);
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.email = email;
    }

    public User(String username, String password, boolean enabled, boolean accountNonExpired,
                boolean credentialsNonExpired, boolean accountNonLocked,
                Collection<? extends GrantedAuthority> authorities,
                String surname, String name, String patronymic, String email) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.email = email;
    }

    /**
     * Получение имени пользователя в формате: Фамиилия Имя Отчество
     */
    public String getUserFullName() {
        List<String> fullNameParts = new LinkedList<>();
        if (!StringUtils.isEmpty(surname))
            fullNameParts.add(surname);
        if (!StringUtils.isEmpty(name))
            fullNameParts.add(name);
        if (!StringUtils.isEmpty(patronymic))
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
        if (!StringUtils.isEmpty(surname)) {
            sb.append(getSurname().trim()).append(' ');
            if (!StringUtils.isEmpty(name))
                sb.append(name.trim().toUpperCase().charAt(0)).append('.');
            if (!StringUtils.isEmpty(patronymic))
                sb.append(patronymic.trim().toUpperCase().charAt(0)).append('.');
        } else if (!StringUtils.isEmpty(name))
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
        if (!StringUtils.isEmpty(name))
            parts.add(name);
        if (!StringUtils.isEmpty(surname))
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
}
