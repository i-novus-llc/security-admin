package net.n2oapp.security.admin.impl.service;

import liquibase.util.StringUtils;
import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserDetailsToken;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@Transactional
@Qualifier("EsiaUserDetailsServiceImpl")
public class EsiaUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Value("${access.esia.sync-fio:true}")
    private Boolean synchronizeFio;

    @Override
    public User loadUserDetails(UserDetailsToken userDetails) {
        UserEntity userEntity = userRepository.findOneBySnilsIgnoreCase(userDetails.getUsername()).orElse(null);
        if (isNull(userEntity)) {
            throw new UserException("exception.userFromEsiaNotFound");
        } else if (!StringUtils.equalsIgnoreCaseAndEmpty(userEntity.getExtSys(), userDetails.getExtSys())) {
            throw new UserException("exception.ssoOtherSystemUser");
        }
        userEntity.setIsActive(true);
        if (synchronizeFio) {
            userEntity.setName(userDetails.getName());
            userEntity.setSurname(userDetails.getSurname());
            userEntity.setPatronymic(userDetails.getPatronymic());
        }
        return model(userRepository.save(userEntity));
    }

    private User model(UserEntity entity) {
        if (entity == null) return null;
        User model = new User();
        model.setId(entity.getId());
        model.setExtUid(entity.getExtUid());
        model.setUsername(entity.getUsername());
        model.setName(entity.getName());
        model.setSurname(entity.getSurname());
        model.setPatronymic(entity.getPatronymic());
        model.setIsActive(entity.getIsActive());
        model.setEmail(entity.getEmail());
        model.setExtSys(entity.getExtSys());
        StringBuilder builder = new StringBuilder();
        if (entity.getSurname() != null) {
            builder.append(entity.getSurname()).append(" ");
        }
        if (entity.getName() != null) {
            builder.append(entity.getName()).append(" ");
        }
        if (entity.getPatronymic() != null) {
            builder.append(entity.getPatronymic());
        }
        model.setFio(builder.toString());
        if (entity.getRoleList() != null) {
            model.setRoles(entity.getRoleList().stream().map(this::model).collect(Collectors.toList()));
        }
        return model;
    }

    private Role model(RoleEntity entity) {
        if (entity == null) return null;
        Role model = new Role();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        if (entity.getPermissionList() != null) {
            model.setPermissions(entity.getPermissionList().stream().map(this::model).collect(Collectors.toList()));
        }
        return model;
    }

    private Permission model(PermissionEntity entity) {
        if (entity == null) return null;
        Permission model = new Permission();
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        model.setParentCode(entity.getParentCode());
        return model;
    }
}
