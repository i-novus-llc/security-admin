package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.rest.api.PermissionRestService;

import java.util.List;

/**
 * Прокси реализация сервиса управления правами доступа, для вызова rest
 */
public class PermissionServiceRestClient implements PermissionService {

    private PermissionRestService client;

    public PermissionServiceRestClient(PermissionRestService client) {
        this.client = client;
    }

    @Override
    public Permission create(Permission permission) {
        return client.create(permission);
    }

    @Override
    public Permission update(Permission permission) {
        return client.update(permission);
    }

    @Override
    public void delete(String code) {
        client.delete(code);
    }

    @Override
    public Permission getById(String code) {
        return client.getById(code);
    }

    @Override
    public List<Permission> getAll() {
        return client.getAll(null, null).getContent();
    }

    @Override
    public List<Permission> getAllByParentCode(String parentCode) {
        return client.getAll(parentCode, null).getContent();
    }

    @Override
    public List<Permission> getAllByParentIdIsNull() {
        return client.getAll(null, true).getContent();
    }
}
