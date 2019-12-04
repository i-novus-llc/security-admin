package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
public class RoleServerLoader extends RepositoryServerLoader<RoleForm, RoleEntity, Integer> {

    private RoleRepository roleRepository;

    public RoleServerLoader(RoleRepository repository) {
        super(repository,
                RoleServerLoader::map,
                systemCode -> repository.findBySystemCode(new SystemEntity(systemCode)),
                RoleEntity::getId);
        roleRepository = repository;
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
    @Transactional
    public void load(List<RoleForm> data, String subject) {
        List<RoleEntity> fresh = map(data, subject);
        save(fresh);
        delete(fresh, subject);
    }

    @Override
    protected void save(List<RoleEntity> fresh) {
        for (RoleEntity role : fresh) {
            RoleEntity entity = roleRepository.findOneByCode(role.getCode());
            if (entity == null) {
                roleRepository.save(role);
            } else role.setId(entity.getId());
        }
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
