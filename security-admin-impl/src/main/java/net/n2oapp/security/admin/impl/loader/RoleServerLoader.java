package net.n2oapp.security.admin.impl.loader;

import lombok.extern.slf4j.Slf4j;
import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.platform.loader.server.ServerLoader;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RoleServerLoader implements ServerLoader<RoleForm> {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @Override
    @Transactional
    public void load(List<RoleForm> uploadedData, String subject) {
        List<RoleEntity> old = roleRepository.findAll();
        List<RoleForm> fresh = prepareFreshRoles(uploadedData, subject, old);
        for (RoleForm role : fresh) {
            try {
                roleService.create(role);
            } catch (IllegalArgumentException e) {
                log.warn("Ошибка при добавлении роли в keycloak: {}", e.getMessage());
            }
        }
        uploadedData.removeAll(fresh);
        uploadedData.forEach(roleService::update);
    }

    private List<RoleForm> prepareFreshRoles(List<RoleForm> uploadedData, String systemCode, List<RoleEntity> old) {
        return uploadedData.stream().filter(r -> {
            for (RoleEntity oldRole : old) {
                if (r.getName() == null)
                    throw new UserException("exception.roleNameIsNull");
                if (r.getCode().equals(oldRole.getCode()) || r.getName().equals(oldRole.getName()))
                    return false;
            }
            r.setSystemCode(systemCode);
            return true;
        }).collect(Collectors.toList());
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
