package net.n2oapp.security.admin.api.criteria;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    private String systemCode;
    private String userLevel;
    private Integer regionId;
    private Integer organizationId;
    private Integer departmentId;
    private String extSys;

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds != null ? roleIds : new ArrayList<>();
    }

}
