package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.BaseCriteria;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис прав доступа
 */
@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;


    @Override
    public Permission create(Permission permission) {
        return model(permissionRepository.save(entity(permission)));
    }

    @Override
    public Permission update(Permission permission) {
        return model(permissionRepository.save(entity(permission)));
    }

    @Override
    public void delete(Integer id) {
        permissionRepository.delete(id);

    }

    @Override
    public Permission getById(Integer id) {
        PermissionEntity permissionEntity = permissionRepository.findOne(id);
        return model(permissionEntity);
    }

    @Override
    public Page<Permission> findAll(BaseCriteria criteria) {
        Page<PermissionEntity> all = permissionRepository.findAll(criteria);
        return all.map(this::model);
    }

    private PermissionEntity entity(Permission model) {
        if (model == null) return null;
        PermissionEntity entity = new PermissionEntity();
        entity.getId();
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setParentId(model.getParentId());
        return entity;
    }

    private Permission model(PermissionEntity entity) {
        if (entity == null) return null;
        Permission model = new Permission();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        model.setParentId(entity.getParentId());
        return model;
    }
}