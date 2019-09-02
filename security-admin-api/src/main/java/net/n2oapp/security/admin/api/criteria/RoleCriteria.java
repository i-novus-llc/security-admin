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
    private List<Integer> permissionIds;

    public void setPermissionIds(List<Integer> permissionIds) {
        this.permissionIds = permissionIds != null ? permissionIds : new ArrayList<>();
    }
}
