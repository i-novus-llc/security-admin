package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
public class RoleServerLoader extends RepositoryServerLoader<RoleForm, RoleEntity, Integer> {

    public RoleServerLoader(RoleRepository repository) {
        super(repository,
                RoleServerLoader::map,
                systemCode -> repository.findBySystemCode(new SystemEntity(systemCode)),
                RoleEntity::getId);
    }

    private static RoleEntity map(RoleForm form, String systemCode) {
        RoleEntity entity = new RoleEntity();
        entity.setCode(form.getCode());
        entity.setName(form.getName());
        entity.setDescription(form.getDescription());
        if (form.getPermissions() != null) {
            entity.setPermissionList(form.getPermissions().stream()
                    .filter(s -> !s.startsWith("$"))
                    .map(PermissionEntity::new)
                    .collect(Collectors.toList()));
        }
        entity.setSystemCode(new SystemEntity(systemCode));
        if (nonNull(form.getUserLevel()))
            entity.setUserLevel(UserLevel.valueOf(form.getUserLevel()));
        return entity;
    }

    @Override
    public String getTarget() {
        return "roles";
    }

    @Override
    public Class getDataType() {
        return RoleForm.class;
    }
}
