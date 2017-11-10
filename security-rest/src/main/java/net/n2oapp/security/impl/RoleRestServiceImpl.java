package net.n2oapp.security.impl;

import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.rest.RoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

/**
 * Реализация сервиса управления ролями пользоватлями
 */
public class RoleRestServiceImpl implements RoleRestService {
    @Autowired
    private RoleService service;

    @Override
    public Page<Role> search(RoleCriteria criteria) {
        return   service.findAll(criteria);
    }

    @Override
    public void create(Role role) {
        service.create(role);
    }

    @Override
    public void update(Integer id) {
        //service.update(id);
    }

    @Override
    public void delete(Integer id) {
        service.delete(id);
    }
}
