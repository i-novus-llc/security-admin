package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

import java.util.UUID;

/**
 * Критерий фильтрации уполномоченных лиц банка
 */
@Data
public class EmployeeBankCriteria extends BaseCriteria {
    private UUID bankId;
}
