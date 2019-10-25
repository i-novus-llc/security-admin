package net.n2oapp.security.auth.common;

import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Пользователь с расширенными атрибутами
 */
public class User extends org.springframework.security.core.userdetails.User {
    private static final String DEFAULT_ROLE = "ROLE_USER";

    private String surname;
    private String name;
    private String patronymic;
    private String email;
    private String organization;
    private String region;
    private String department;
    private String userLevel;

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public List<String> getRoles() {
        return getAuthorities()
                .stream()
                .filter(a -> a instanceof RoleGrantedAuthority)
                .map(a -> ((RoleGrantedAuthority) a).getRole()).collect(Collectors.toList());
    }

    public List<String> getPermissions() {
        return getAuthorities()
                .stream()
                .filter(a -> a instanceof PermissionGrantedAuthority)
                .map(p -> ((PermissionGrantedAuthority) p).getPermission()).collect(Collectors.toList());
    }
}
