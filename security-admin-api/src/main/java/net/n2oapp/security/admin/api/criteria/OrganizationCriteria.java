package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

/**
 * Критерий фильтрации организаций
 */
@Data
public class OrganizationCriteria extends BaseCriteria {
    private String name;
}
