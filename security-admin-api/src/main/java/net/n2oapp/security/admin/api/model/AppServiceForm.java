package net.n2oapp.security.admin.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Служба
 */
@Data
@NoArgsConstructor
public class AppServiceForm {
    private String code;
    private String name;
    private String systemCode;
}