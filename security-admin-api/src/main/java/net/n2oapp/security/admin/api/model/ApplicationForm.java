package net.n2oapp.security.admin.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Приложение
 */
@Data
@NoArgsConstructor
public class ApplicationForm {
    private String code;
    private String name;
    private String systemCode;
}