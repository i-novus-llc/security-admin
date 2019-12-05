package net.n2oapp.security.admin.api.criteria;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Критерий фильтрации систем
 */
@Data
@NoArgsConstructor
public class SystemCriteria extends BaseCriteria {
    private String code;
    private String name;
    private List<String> codeList;
    private Boolean publicAccess;

    public SystemCriteria(int page, int size) {
        super(page, size);
    }
}
