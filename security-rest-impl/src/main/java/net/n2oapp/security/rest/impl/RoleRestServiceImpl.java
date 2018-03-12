package net.n2oapp.security.rest.impl;

import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.rest.api.RoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import net.n2oapp.security.admin.api.model.Permission;

import java.util.HashSet;
import java.util.Set;

/**
 * Реализация сервиса управления ролями пользоватлями
 */
@Controller
public class RoleRestServiceImpl implements RoleRestService {
    @Autowired
    private RoleService service;

    @Override
    public Page<Role> search( Integer page, Integer size,String name,String description) {
        RoleCriteria criteria = new RoleCriteria(page-1, size);

        criteria.setName(name);
        criteria.setDescription(description);
        Permission permission = new Permission();
        permission.setId(1);
        Set<Integer> permissions = new HashSet<Integer>();
        permissions.add(permission.getId());
        //criteria.setPermissionIds(permissions);
        return   service.findAll(criteria);
    }

    @Override
    public Role getById(Integer id) {
        return service.getById(id);
    }

    @Override
    public Role create(Role role) {
        return  service.create(role);
    }

    @Override
    public Role update( Role role) {
        return service.update(role);

    }

    @Override
    public void delete(Integer id) {
        service.delete(id);
    }
}
