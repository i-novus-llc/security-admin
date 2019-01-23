package net.n2oapp.security.admin.api.model;

import lombok.Data;
import net.n2oapp.security.admin.api.model.department.DepartmentUpdateForm;

/**
 * Модель уполномоченного лица ДОМ.РФ для actions
 */
@Data
public class EmployeeDomrfForm {
    private Integer id;
    private String position;
    private UserForm user;
    private DepartmentUpdateForm department;
}
