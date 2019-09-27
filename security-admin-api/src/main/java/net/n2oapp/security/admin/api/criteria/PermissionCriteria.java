package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

/**
 * Критерий фильтрации прав доступа
 */
@Data
public class PermissionCriteria extends BaseCriteria {
    private String systemCode;

    public PermissionCriteria() {

    }
}
