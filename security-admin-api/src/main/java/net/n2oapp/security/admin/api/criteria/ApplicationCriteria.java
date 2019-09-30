package net.n2oapp.security.admin.api.criteria;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Критерий фильтрации приложений
 */
@Data
@NoArgsConstructor
public class ApplicationCriteria extends BaseCriteria {
    private String systemCode;

    public ApplicationCriteria(int page, int size) {
        super(page, size);
    }
}
