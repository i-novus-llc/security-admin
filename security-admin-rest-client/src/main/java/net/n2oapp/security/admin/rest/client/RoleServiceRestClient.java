package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.rest.api.criteria.RestRoleCriteria;
import net.n2oapp.security.admin.rest.client.feign.RoleServiceFeignClient;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления ролями, для вызова rest
 */
public class RoleServiceRestClient implements RoleService {

    private RoleServiceFeignClient client;

    public RoleServiceRestClient(RoleServiceFeignClient client) {
        this.client = client;
    }

    @Override
    public Role create(RoleForm role) {
        return client.create(role);
    }

    @Override
    public Role update(RoleForm role) {
        return client.update(role);
    }

    @Override
    public void delete(Integer id) {
        client.delete(id);
    }

    @Override
    public Role getById(Integer id) {
        return client.getById(id);
    }

    @Override
    public Role getByIdWithSystem(Integer id) {
        return client.getByIdWithSystem(id);
    }

    @Override
    public Page<Role> findAll(RoleCriteria criteria) {
        return client.findAll(toRestCriteria(criteria));
    }

    public Page<Role> findAllForForm(RoleCriteria criteria) {
        criteria.setForForm(true);
        return findAll(criteria);
    }

    private RestRoleCriteria toRestCriteria(RoleCriteria criteria) {
        RestRoleCriteria roleCriteria = new RestRoleCriteria();
        roleCriteria.setPage(criteria.getPageNumber());
        roleCriteria.setSize(criteria.getPageSize());
        roleCriteria.setName(criteria.getName());
        roleCriteria.setDescription(criteria.getDescription());
        roleCriteria.setPermissionCodes(criteria.getPermissionCodes());
        roleCriteria.setSystemCodes(criteria.getSystemCodes());
        roleCriteria.setOrders(criteria.getOrders());
        roleCriteria.setUserLevel(criteria.getUserLevel());
        roleCriteria.setForForm(criteria.getForForm());
        roleCriteria.setGroupBySystem(criteria.getGroupBySystem());
        roleCriteria.setOrgRoles(criteria.getOrgRoles());
        roleCriteria.setFilterByOrgRoles(criteria.getFilterByOrgRoles());
        return roleCriteria;
    }
}
