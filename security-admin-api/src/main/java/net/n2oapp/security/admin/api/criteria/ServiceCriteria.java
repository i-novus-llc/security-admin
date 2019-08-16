package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

/**
 * Критерий фильтрации служб
 */
@Data
public class ServiceCriteria extends BaseCriteria {
    private String systemCode;
}
