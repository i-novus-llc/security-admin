package net.n2oapp.security.admin.api.criteria;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Критерий фильтрации типов аккаунтов
 */
@Getter
@Setter
@NoArgsConstructor
public class AccountTypeCriteria extends BaseCriteria {
    private String name;
    private String userLevel;
}
