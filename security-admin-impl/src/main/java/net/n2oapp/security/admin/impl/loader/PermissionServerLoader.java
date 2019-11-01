package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import org.springframework.stereotype.Component;

@Component
public class PermissionServerLoader extends RepositoryServerLoader<Permission, PermissionEntity, String> {

    public PermissionServerLoader(PermissionRepository repository) {
        super(repository,
                PermissionServerLoader::map,
                systemCode -> repository.findBySystemCodeOrderByCodeDesc(new SystemEntity(systemCode)),
                PermissionEntity::getCode);
    }

    private static PermissionEntity map(Permission model, String systemCode) {
        PermissionEntity entity = new PermissionEntity();
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setParentCode(model.getParentCode());
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
