package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import org.springframework.stereotype.Component;

@Component
public class PermissionLoader extends RepositoryServerLoader<Permission, PermissionEntity, String> {

    public PermissionLoader(PermissionRepository repository) {
        super(repository,
                PermissionLoader::map,
                systemCode -> repository.findBySystemCodeOrderByCodeDesc(new SystemEntity(systemCode)),
                PermissionEntity::getCode);
    }

    @Override
    public Class<?> getTargetClass() {
        return PermissionLoader.class;
    }

    private static PermissionEntity map(Permission model, String systemCode) {
        PermissionEntity entity = new PermissionEntity();
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setParentCode(model.getParentCode());
        entity.setSystemCode(new SystemEntity(systemCode));
        return entity;
    }
}
