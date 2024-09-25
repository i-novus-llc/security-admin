package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.PermissionCriteria;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.PermissionUpdateForm;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.rest.api.criteria.RestPermissionCriteria;
import net.n2oapp.security.admin.rest.client.feign.PermissionServiceFeignClient;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Прокси реализация сервиса управления правами доступа, для вызова rest
 */
public class PermissionServiceRestClient implements PermissionService {

    private PermissionServiceFeignClient client;

    public PermissionServiceRestClient(PermissionServiceFeignClient client) {
        this.client = client;
    }

    @Override
    public Permission create(Permission permission) {
        return client.create(permission);
    }

    @Override
    public Permission update(PermissionUpdateForm permission) {
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
    public Page<Permission> getAll(PermissionCriteria criteria) {
        RestPermissionCriteria permissionCriteria = new RestPermissionCriteria();
        permissionCriteria.setName(criteria.getName());
        permissionCriteria.setCode(criteria.getCode());
        permissionCriteria.setPage(criteria.getPageNumber());
        permissionCriteria.setSize(criteria.getPageSize());
        permissionCriteria.setSystemCode(criteria.getSystemCode());
        permissionCriteria.setOrders(criteria.getOrders());
        permissionCriteria.setUserLevel(criteria.getUserLevel());
        permissionCriteria.setForForm(criteria.getForForm());
        permissionCriteria.setWithSystem(criteria.getWithSystem());
        permissionCriteria.setWithoutParent(criteria.getWithoutParent());
        return client.getAll(null, permissionCriteria);
    }

    @Override
    public List<Permission> getAllByParentCode(String parentCode) {
        return client.getAll(parentCode, new RestPermissionCriteria()).getContent();
    }
}
