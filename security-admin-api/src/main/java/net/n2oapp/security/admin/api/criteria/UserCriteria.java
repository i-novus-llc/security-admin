package net.n2oapp.security.admin.api.criteria;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

/**
 * Критерий фильтрации пользователей
 */

public class UserCriteria  extends PageRequest {
    private String username;
    private String surname;
    private String name;
    private String patronymic;
    private Boolean isActive;
    private Set<Integer> roleIds;

    public UserCriteria(int page, int size) {
        super(page, size);
    }

    public UserCriteria(int page, int size, Sort.Direction direction, String property) {
        super(page, size, direction, property);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Set<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Integer> roleIds) {
        this.roleIds = roleIds;
    }
}
