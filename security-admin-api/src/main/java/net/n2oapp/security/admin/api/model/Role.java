package net.n2oapp.security.admin.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Роль
 */
@Data
@NoArgsConstructor
public class Role {
    private Integer id;
    private String name;
    private String code;
    private String description;
    private List<Permission> permissions;


}
