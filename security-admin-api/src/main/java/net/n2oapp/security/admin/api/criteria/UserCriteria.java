package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Критерий фильтрации пользователей
 */
@Data
public class UserCriteria extends BaseCriteria {
    private String username;
    private String fio;
    private Boolean isActive;
    private List<Integer> roleIds;
    private String password;
    private UUID bank;

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds != null ? roleIds : new ArrayList<>();
    }
}
