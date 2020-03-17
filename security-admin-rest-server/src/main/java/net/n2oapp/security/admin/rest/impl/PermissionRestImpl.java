package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.PermissionUpdateForm;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.rest.api.PermissionRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestPermissionCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;

/**
 * Реализация REST сервиса управления правами доступа
 */
@Controller
@ConditionalOnProperty(name = "access.permission.enabled", havingValue = "true")
public class PermissionRestImpl implements PermissionRestService {
    @Autowired
    private PermissionService service;

    @Override
    public Page<Permission> getAll(String parentCode, RestPermissionCriteria criteria) {
        return parentCode != null ? new PageImpl<>(service.getAllByParentCode(parentCode)) : service.getAll(criteria);
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
    public Permission update(PermissionUpdateForm permission) {
        return service.update(permission);
    }

    @Override
    public void delete(String code) {
        service.delete(code);
    }
}
