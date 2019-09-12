package net.n2oapp.security.admin.rest.api.criteria;

import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Модель фильтрации ролей для rest вызовов
 */
public class RestRoleCriteria extends RoleCriteria {

    @QueryParam("page")
    @Override
    public void setPage(int page) {
        super.setPage(page);
    }

    @QueryParam("size")
    @Override
    public void setSize(int size) {
        super.setSize(size);
    }

    @QueryParam("sort")
    @Override
    public void setOrders(List<Sort.Order> orders) {
        super.setOrders(orders);
    }

    @QueryParam("name")
    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @QueryParam("description")
    @Override
    public void setDescription(String description) {
        super.setDescription(description);
    }

    @QueryParam("permissions")
    @Override
    public void setPermissionIds(List<Integer> permissionIds) {
        super.setPermissionIds(permissionIds);
    }

    @QueryParam("permissionCodes")
    @Override
    public void setPermissionCodes(List<String> permissionCodes) {
        super.setPermissionCodes(permissionCodes);
    }
}
