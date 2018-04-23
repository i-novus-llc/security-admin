package net.n2oapp.security.admin.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Право доступа
 */
@Data
@NoArgsConstructor
public class Permission {
    private Integer id;
    private String name;
    private String code;
    private Integer parentId;
    private Boolean hasChildren;

}
