package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Критерий фильтрации уполномоченных лиц банка
 */
@Data
public class EmployeeBankCriteria extends BaseCriteria {
    private String bankId;
}
