package net.n2oapp.security.admin.api.criteria;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Set;

/**
 * Created by otihonova on 02.11.2017.
 */
public class RoleCriteria extends PageRequest {
    private String name;
    private String description;
    private Set<Integer> permissionIds;

    public RoleCriteria(int page, int size) {
        super(page, size);
    }

    public RoleCriteria(int page, int size, Sort.Direction direction, String property) {
        super(page, size, direction, property);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Integer> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(Set<Integer> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
