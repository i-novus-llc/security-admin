package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Критерий фильтрации ролей
 */
@Data
public class RoleCriteria extends BaseCriteria {
    private String name;
    private String description;
    private List<String> permissionCodes;
    private List<String> systemCodes;
    private String userLevel;
    private Boolean forForm;

    public void setPermissionCodes(List<String> permissionCodes) {
        this.permissionCodes = permissionCodes != null ? permissionCodes : new ArrayList<>();
    }
}
