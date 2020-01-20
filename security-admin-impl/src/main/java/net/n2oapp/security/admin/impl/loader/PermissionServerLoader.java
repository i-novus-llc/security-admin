package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.ServerLoader;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PermissionServerLoader implements ServerLoader<Permission> {

    @Autowired
    PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void load(List<Permission> uploadedData, String subject) {
        List<PermissionEntity> old = permissionRepository.findBySystemCodeOrderByCodeDesc(new SystemEntity(subject));
        List<PermissionEntity> fresh = uploadedData.stream().map(permission -> map(permission, subject)).collect(Collectors.toList());
        permissionRepository.saveAll(fresh);
        old.removeAll(fresh);
        fresh.forEach(permissionEntity -> old.remove(old.stream().
                filter(permissionOldEntity -> permissionOldEntity.getCode().equals(permissionEntity.getCode())).findFirst().orElse(null)));
        old.forEach(permissionEntity -> permissionEntity.getRoleList().forEach(roleEntity -> roleEntity.getPermissionList().remove(permissionEntity)));
        permissionRepository.deleteAll(old);
    }

    private static PermissionEntity map(Permission model, String systemCode) {
        PermissionEntity entity = new PermissionEntity();
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        if (model.getParent() != null && model.getParent().getCode() != null)
            entity.setParentPermission(new PermissionEntity(model.getParent().getCode()));
        entity.setSystemCode(new SystemEntity(systemCode));
        entity.setUserLevel(model.getUserLevel());
        return entity;
    }

    @Override
    public String getTarget() {
        return "permissions";
    }

    @Override
    public Class<Permission> getDataType() {
        return Permission.class;
    }
}
