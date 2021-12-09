package net.n2oapp.security.admin.impl.loader;

import lombok.extern.slf4j.Slf4j;
import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.platform.loader.server.ServerLoader;
import net.n2oapp.platform.loader.server.ServerLoaderSettings;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Component
@Slf4j
public class RoleServerLoader extends ServerLoaderSettings<RoleForm> implements ServerLoader<RoleForm> {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @Override
    @Transactional
    public void load(List<RoleForm> uploadedData, String subject) {
        List<RoleEntity> old = roleRepository.findAll();
        List<RoleForm> fresh = prepareFreshRoles(uploadedData, subject, old);
        if (isCreateRequired()) {
            for (RoleForm role : fresh) {
                try {
                    roleService.create(role);
                } catch (IllegalArgumentException e) {
                    log.warn("Ошибка при добавлении роли в keycloak: {}", e.getMessage());
                }
            }
        }
        if (isUpdateRequired())
            uploadedData.stream().filter(not(fresh::contains)).forEach(roleService::update);
    }

    private List<RoleForm> prepareFreshRoles(List<RoleForm> uploadedData, String systemCode, List<RoleEntity> old) {
        return uploadedData.stream().filter(r -> {
            r.setSystemCode(systemCode);
            if (r.getName() == null)
                throw new UserException("exception.roleNameIsNull");

            for (RoleEntity oldRole : old) {
                if (r.getCode().equals(oldRole.getCode()) || r.getName().equals(oldRole.getName())) {
                    r.setId(oldRole.getId());
                    return false;
                }
            }

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
