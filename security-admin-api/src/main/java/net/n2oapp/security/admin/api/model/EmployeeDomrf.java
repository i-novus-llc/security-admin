package net.n2oapp.security.admin.api.model;

import lombok.Data;
import net.n2oapp.security.admin.api.model.department.Department;

import java.util.UUID;

/**
 * Модель уполномоченного лица ДОМ.РФ для показа на UI
 */
@Data
public class EmployeeDomrf {
    private UUID id;
    private String employeeName;
    private String position;
    private User user;
    private Department department;

}
