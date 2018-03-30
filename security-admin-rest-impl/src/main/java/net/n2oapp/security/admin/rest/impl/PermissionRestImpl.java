package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.rest.api.PermissionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;


/**
 * Реализация REST сервиса управления правами доступа
 */
@Controller
public class PermissionRestImpl implements PermissionRestService {
    @Autowired
    private PermissionService service;

    @Override
    public Page<Permission> getAll(Integer page, Integer size) {
        return service.getAll(new PageRequest(page - 1, size, Sort.Direction.DESC, "id"));
    }

    @Override
    public Permission getById(Integer id) {
        return service.getById(id);
    }
}
