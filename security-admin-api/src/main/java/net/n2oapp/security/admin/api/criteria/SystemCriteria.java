package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

/**
 * Критерий фильтрации систем
 */
@Data
public class SystemCriteria extends BaseCriteria {
    private String code;
    private String name;
}
