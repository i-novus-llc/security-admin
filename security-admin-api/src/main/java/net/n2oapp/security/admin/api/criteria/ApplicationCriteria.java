package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

/**
 * Критерий фильтрации приложений
 */
@Data
public class ApplicationCriteria extends BaseCriteria {
    private String systemCode;
}
