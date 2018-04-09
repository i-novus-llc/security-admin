package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.rest.api.PermissionRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestBaseCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

/**
 * Реализация REST сервиса управления правами доступа
 */
@Controller
public class PermissionRestImpl implements PermissionRestService {
    @Autowired
    private PermissionService service;

    @Override
    public Page<Permission> findAll(RestBaseCriteria criteria) {
        return service.findAll(criteria);
    }

    @Override
    public Permission create(Permission permission) {
        return service.create(permission);
    }

    @Override
    public Permission update(Permission permission) {
        return service.update(permission);
    }

    @Override
    public void delete(Integer id) {
        service.delete(id);
    }

    @Override
    public Permission getById(Integer id) {
        return service.getById(id);
    }
}
