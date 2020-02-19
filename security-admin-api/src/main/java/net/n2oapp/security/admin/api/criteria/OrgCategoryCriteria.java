package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

/**
 * Критерий фильтрации категорий организаций
 */
@Data
public class OrgCategoryCriteria extends BaseCriteria {
    private String name;
}
