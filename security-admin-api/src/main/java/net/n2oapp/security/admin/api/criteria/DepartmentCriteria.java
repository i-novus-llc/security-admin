package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

/**
 * Критерий фильтрации департаментов
 */
@Data
public class DepartmentCriteria extends BaseCriteria {
    private String name;
}
