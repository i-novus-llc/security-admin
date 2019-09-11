package net.n2oapp.security.admin.api.criteria;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Критерий фильтрации ролей
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleCriteria extends BaseCriteria {
    private String name;
    private String description;
    private List<String> permissionCodes;

    public void setPermissionCodes(List<String> permissionCodes) {
        this.permissionCodes = permissionCodes != null ? permissionCodes : new ArrayList<>();
    }
}
