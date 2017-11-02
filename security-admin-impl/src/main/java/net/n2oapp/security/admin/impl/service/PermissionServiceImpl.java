package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by otihonova on 31.10.2017.
 */
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Integer create(Permission permission) {
        return permissionRepository.save(convertToPermissionEntity(permission)).getId();
    }

    @Override
    public Integer update(Permission permission) {
        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setId(permission.getId());
        permissionEntity.setCode(permission.getCode());
        permissionEntity.setName(permission.getName());
        permissionEntity.setParentId(permission.getParentId());
        return permissionRepository.save(permissionEntity).getId();
    }

    @Override
    public void delete(Integer id) {
        permissionRepository.delete(id);

    }

    @Override
    public Permission getById(Integer id) {
        PermissionEntity permissionEntity=permissionRepository.findOne(id);
        return convertToPermission(permissionEntity);
    }
    private PermissionEntity convertToPermissionEntity (Permission permission) {
        /* PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setId(permission.getId());
        permissionEntity.setCode(permission.getCode());
        permissionEntity.setName(permission.getName());
        permissionEntity.setParentId(permission.getParentId());
        */
        PermissionEntity permissionEntity = modelMapper.map(permission, PermissionEntity.class);
        return permissionEntity;
    }

    private Permission convertToPermission (PermissionEntity permissionEntity){
        Permission permission = modelMapper.map(permissionEntity, Permission.class);
        return permission;

    }
}
