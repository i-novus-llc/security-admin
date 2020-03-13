package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.ServerLoader;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleServerLoader implements ServerLoader<RoleForm> {

    @Autowired
    private RoleRepository roleRepository;

    @Value("${access.permission.enabled}")
    private Boolean permissionEnabled;

    @Override
    @Transactional
    public void load(List<RoleForm> uploadedData, String subject) {
        List<RoleEntity> old = roleRepository.findBySystemCode(new SystemEntity(subject));
        List<RoleEntity> fresh = getFresh(uploadedData, subject, old);
        roleRepository.saveAll(fresh);
        delete(old);
    }

    private List<RoleEntity> getFresh(List<RoleForm> fresh, String systemCode, List<RoleEntity> old) {
        List<RoleEntity> freshEntities = new ArrayList<>(fresh.size());
        for (RoleForm form : fresh) {
            RoleEntity roleEntity = old.stream().filter(r -> r.getCode().equals(form.getCode())).findFirst().orElse(null);
            if (roleEntity == null)
                roleEntity = new RoleEntity();
            else
                old.remove(roleEntity);

            map(form, systemCode, roleEntity);
            freshEntities.add(roleEntity);
        }
        return freshEntities;
    }

    private void map(RoleForm form, String systemCode, RoleEntity entity) {
        entity.setCode(form.getCode());
        entity.setName(form.getName());
        entity.setDescription(form.getDescription());
        if (form.getUserLevel() != null)
            entity.setUserLevel(UserLevel.valueOf(form.getUserLevel()));
        entity.setSystemCode(new SystemEntity(systemCode));

        if (permissionEnabled && form.getPermissions() != null) {
            entity.setPermissionList(form.getPermissions().stream()
                    .map(PermissionEntity::new)
                    .collect(Collectors.toList()));
        }
        if (form.getUserLevel() != null) {
            entity.setUserLevel(UserLevel.valueOf(form.getUserLevel()));
        }
    }

    private void delete(List<RoleEntity> old) {
        old.forEach(roleEntity -> roleEntity.getUserList().forEach(userEntity -> userEntity.getRoleList().remove(roleEntity)));
        roleRepository.deleteAll(old);
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
