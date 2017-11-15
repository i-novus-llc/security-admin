package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис прав доступа
 */
@Service

public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Permission create(Permission permission) {
        return convertToPermission(permissionRepository.save(convertToPermissionEntity(permission)));
    }

    @Override
    public Permission update(Permission permission) {
        return convertToPermission(permissionRepository.save(convertToPermissionEntity(permission)));
    }

    @Override
    public void delete(Integer id) {
        permissionRepository.delete(id);

    }

    @Override
    public Permission getById(Integer id) {
        PermissionEntity permissionEntity = permissionRepository.findOne(id);
        return convertToPermission(permissionEntity);
    }

    private PermissionEntity convertToPermissionEntity(Permission permission) {
        PermissionEntity permissionEntity = modelMapper.map(permission, PermissionEntity.class);
        return permissionEntity;
    }

    private Permission convertToPermission(PermissionEntity permissionEntity) {
        Permission permission = modelMapper.map(permissionEntity, Permission.class);
        return permission;

    }
}