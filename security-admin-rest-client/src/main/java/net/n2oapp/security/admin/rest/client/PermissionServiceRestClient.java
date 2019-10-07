package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.PermissionCriteria;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.rest.api.PermissionRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestPermissionCriteria;

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
    public Permission getByCode(String code) {
        return client.getById(code);
    }

    @Override
    public List<Permission> getAll(PermissionCriteria criteria) {
        RestPermissionCriteria permissionCriteria = new RestPermissionCriteria();
        permissionCriteria.setPage(criteria.getPageNumber());
        permissionCriteria.setSize(criteria.getPageSize());
        permissionCriteria.setSystemCode(criteria.getSystemCode());
        permissionCriteria.setOrders(criteria.getOrders());
        permissionCriteria.setUserLevel(criteria.getUserLevel());
        return client.getAll(null, null, permissionCriteria).getContent();
    }

    @Override
    public List<Permission> getAllByParentCode(String parentCode) {
        return client.getAll(parentCode, null, new RestPermissionCriteria()).getContent();
    }

    @Override
    public List<Permission> getAllByParentIdIsNull() {
        return client.getAll(null, true, new RestPermissionCriteria()).getContent();
    }
}
