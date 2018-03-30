package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.rest.api.RoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;

import java.util.List;


/**
 * Реализация REST сервиса управления ролями пользоватлями
 */
@Controller
public class RoleRestServiceImpl implements RoleRestService {
    @Autowired
    private RoleService service;

    @Override
    public Page<Role> search(Integer page, Integer size, String name, String description, List<Integer> permissions) {
        RoleCriteria criteria = new RoleCriteria(page - 1, size, Sort.Direction.DESC, "id");
        criteria.setName(name);
        criteria.setDescription(description);
        criteria.setPermissionIds(permissions);
        return service.findAll(criteria);
    }

    @Override
    public Role getById(Integer id) {
        return service.getById(id);
    }

    @Override
    public Role create(RoleForm role) {
        return service.create(role);
    }

    @Override
    public Role update(RoleForm role) {
        return service.update(role);

    }

    @Override
    public void delete(Integer id) {
        service.delete(id);
    }
}
