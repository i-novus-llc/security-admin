package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис прав доступа
 */
@Service("permissionService")
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
    public List<Permission> getAll() {
        return permissionRepository.findAll().stream().map(this::model).collect(Collectors.toList());
    }

    @Override
    public List<Permission> getAllByParentId(Integer parentId) {
        return permissionRepository.findByParentId(parentId).stream().map(this::model).collect(Collectors.toList());
    }

    @Override
    public List<Permission> getAllByParentIdIsNull() {
        return permissionRepository.findByParentIdIsNull().stream().map(this::model).collect(Collectors.toList());
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
        model.setHasChildren(entity.getHasChildren());
        return model;
    }
}