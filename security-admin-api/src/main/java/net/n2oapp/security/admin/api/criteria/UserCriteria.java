package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

import java.util.List;

/**
 * Критерий фильтрации пользователей
 */
@Data
public class UserCriteria extends BaseCriteria {
    private String username;
    private String fio;
    private Boolean isActive;
    private List<Integer> roleIds;
}
