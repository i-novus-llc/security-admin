package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Право доступа
 */
@Data
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Permission {
    private Integer id;
    private String name;
    private String code;
    private Integer parentId;
    private Boolean hasChildren;

}
