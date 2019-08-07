package net.n2oapp.security.user;

import net.n2oapp.security.user.authority.PermissionGrantedAuthority;
import net.n2oapp.security.user.authority.RoleGrantedAuthority;
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
