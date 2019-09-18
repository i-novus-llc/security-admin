package net.n2oapp.security.admin.api.criteria;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Критерий фильтрации пользователей
 */
@Getter
@Setter
public class UserCriteria extends BaseCriteria {
    private String username;
    private String fio;
    private String isActive;
    private List<Integer> roleIds;
    private String password;

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds != null ? roleIds : new ArrayList<>();
    }

}
