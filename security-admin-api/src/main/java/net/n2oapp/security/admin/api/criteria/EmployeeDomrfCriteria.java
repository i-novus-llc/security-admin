package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

import java.util.UUID;

/**
 * Критерий фильтрации уполномоченных лиц ДОМ.РФ
 */
@Data
public class EmployeeDomrfCriteria extends BaseCriteria {
    private UUID departmentId;
}
