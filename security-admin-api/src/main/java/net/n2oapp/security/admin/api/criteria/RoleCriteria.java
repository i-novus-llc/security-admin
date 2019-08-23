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
    private List<String> permissionCode;

    public void setPermissionCode(List<String> permissionCode) {
        this.permissionCode = permissionCode != null ? permissionCode : new ArrayList<>();
    }
}
