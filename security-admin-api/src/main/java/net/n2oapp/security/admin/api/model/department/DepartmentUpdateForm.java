package net.n2oapp.security.admin.api.model.department;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DepartmentUpdateForm extends DepartmentForm {
    private UUID id;
    private LocalDateTime lastActionDate;
}
