package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

import java.util.List;

/**
 * Критерий фильтрации ролей
 */
@Data
public class RoleCriteria extends BaseCriteria {
    private String name;
    private String description;
    private List<Integer> permissionIds;
}
