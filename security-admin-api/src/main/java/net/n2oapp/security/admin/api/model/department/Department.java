package net.n2oapp.security.admin.api.model.department;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.n2oapp.security.admin.api.model.AbstractDto;

@Data
@NoArgsConstructor
public class Department extends AbstractDto {
    private String fullName;
    private String shortName;
}
