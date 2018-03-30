package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private ModelMapper modelMapper;

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
    public Page<Permission> getAll(Pageable pageable) {
        Page<PermissionEntity> all = permissionRepository.findAll(pageable);
        return all.map(this::model);
    }

    private PermissionEntity entity(Permission model) {
        PermissionEntity entity = modelMapper.map(model, PermissionEntity.class);
        return entity;
    }

    private Permission model(PermissionEntity entity) {
        Permission model = modelMapper.map(entity, Permission.class);
        model.setHasChildren(false);
        return model;

    }
}