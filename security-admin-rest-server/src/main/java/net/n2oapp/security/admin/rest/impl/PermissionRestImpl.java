package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.rest.api.PermissionRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestPermissionCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;

/**
 * Реализация REST сервиса управления правами доступа
 */
@Controller
public class PermissionRestImpl implements PermissionRestService {
    @Autowired
    private PermissionService service;

    @Override
    public Page<Permission> getAll(String parentCode, Boolean parentIdIsNull, RestPermissionCriteria criteria) {
        if (Boolean.TRUE.equals(parentIdIsNull))
            return new PageImpl<>(service.getAllByParentIdIsNull());
        return new PageImpl<>(parentCode != null ? service.getAllByParentCode(parentCode) : service.getAll(criteria));
    }

    @Override
    public Permission getById(String code) {
        return service.getByCode(code);
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
    public void delete(String code) {
        service.delete(code);
    }
}
