package net.n2oapp.security.admin.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Система
 */
@Data
@NoArgsConstructor
public class AppSystemForm {
    private String code;
    private String name;
    private String description;
    private List<String> services;


}