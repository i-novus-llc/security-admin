package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

/**
 * Критерий фильтрации прав доступа
 */
@Data
public class PermissionCriteria extends BaseCriteria {
    private String name;
    private String code;
    private String systemCode;
    private String userLevel;
    private Boolean forForm;
    private Boolean withSystem;
    private Boolean withoutParent;

    public PermissionCriteria() {

    }
}
