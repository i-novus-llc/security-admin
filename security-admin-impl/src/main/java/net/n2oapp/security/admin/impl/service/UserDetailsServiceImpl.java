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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@Qualifier("UserDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Value("${access.keycloak.ignore-roles:offline_access,uma_authorization}")
    private String[] ignoreRoles;

    @Override
    public User loadUserDetails(UserDetailsToken userDetails) {
        UserEntity userEntity = userRepository.findOneByUsernameIgnoreCase(userDetails.getUsername());
        if (userEntity == null) {
            userEntity = new UserEntity();
            userEntity.setUsername(userDetails.getUsername());
            userEntity.setExtUid(userDetails.getExtUid());
            userEntity.setEmail(userDetails.getEmail());
            userEntity.setSurname(userDetails.getSurname());
            userEntity.setName(userDetails.getName());
            userEntity.setIsActive(true);
            userEntity.setExtSys(userDetails.getExtSys());
            if (userDetails.getRoleNames() != null) {
                userEntity.setRoleList(userDetails.getRoleNames().stream().map(this::getOrCreateRole).filter(Objects::nonNull).collect(Collectors.toList()));
            }
            userRepository.save(userEntity);
        } else {
            if (!StringUtils.equalsIgnoreCaseAndEmpty(userEntity.getExtSys(), userDetails.getExtSys())) {
                throw new UserException("exception.ssoOtherSystemUser");
            }
            userEntity.setIsActive(true);
            if (userDetails.getExtUid() != null) {
                userEntity.setExtUid(userDetails.getExtUid());
            }
            if (userDetails.getEmail() != null) {
                userEntity.setEmail(userDetails.getEmail());
            }
            if (userDetails.getSurname() != null) {
                userEntity.setSurname(userDetails.getSurname());
            }
            if (userDetails.getName() != null) {
                userEntity.setName(userDetails.getName());
            }
            if (userDetails.getRoleNames() == null) {
                userEntity.getRoleList().clear();
            } else {
                List<String> roleNamesCopy = new ArrayList<>(userDetails.getRoleNames());
                List<RoleEntity> roleForRemove = new ArrayList<>();
                for (RoleEntity r : userEntity.getRoleList()) {
                    if (!userDetails.getRoleNames().contains(r.getCode())) {
                        roleForRemove.add(r);
                    } else {
                        roleNamesCopy.remove(r.getCode());
                    }
                }
                for (String s : ignoreRoles) {
                    roleNamesCopy.remove(s);
                }
                for (RoleEntity r : roleForRemove) {
                    userEntity.getRoleList().remove(r);
                }
                for (String r : roleNamesCopy) {
                    userEntity.getRoleList().add(getOrCreateRole(r));
                }
            }
        }
        return model(userEntity);
    }

    private RoleEntity getOrCreateRole(String name) {
        for (String s : ignoreRoles) {
            if (s.equals(name)) return null;
        }
        RoleEntity roleEntity = roleRepository.findOneByCode(name);
        if (roleEntity == null) {
            roleEntity = new RoleEntity();
            roleEntity.setName(name);
            roleEntity.setCode(name);
            roleEntity = roleRepository.save(roleEntity);
        }
        return roleEntity;
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
