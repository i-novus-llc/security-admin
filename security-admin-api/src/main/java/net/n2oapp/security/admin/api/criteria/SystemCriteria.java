package net.n2oapp.security.admin.api.criteria;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Критерий фильтрации систем
 */
@Data
@NoArgsConstructor
public class SystemCriteria extends BaseCriteria {
    private String code;
    private String name;

    public SystemCriteria(int page, int size) {
        super(page, size);
    }
}
