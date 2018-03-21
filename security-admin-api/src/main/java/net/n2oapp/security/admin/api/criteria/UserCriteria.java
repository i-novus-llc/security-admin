package net.n2oapp.security.admin.api.criteria;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Критерий фильтрации пользователей
 */

public class UserCriteria extends PageRequest {
    private String username;
    private String fio;
    private Boolean isActive;
    private List<Integer> roleIds;



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

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }
}
