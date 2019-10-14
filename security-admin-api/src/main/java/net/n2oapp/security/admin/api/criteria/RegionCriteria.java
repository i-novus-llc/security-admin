package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

/**
 * Критерий фильтрации регионов
 */
@Data
public class RegionCriteria extends BaseCriteria {
    private String name;
}
