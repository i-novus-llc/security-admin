package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserDetailsToken;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.exception.UserNotFoundOauthException;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import org.apache.cxf.interceptor.security.AccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Value("${access.keycloak.ignore-roles:offline_access,uma_authorization}")
    private String[] ignoreRoles;

    private Boolean createUser = true;

    private List<String> defaultRoles = new ArrayList<>();

    private Boolean updateUser = true;

    private Boolean updateRoles = true;

    @Override
    public User loadUserDetails(UserDetailsToken userDetails) {
        UserEntity userEntity = userRepository.findOneByUsernameIgnoreCase(userDetails.getUsername());
        if (userEntity == null && createUser) {
            userEntity = new UserEntity();
            userEntity.setUsername(userDetails.getUsername());
            userEntity.setExtUid(userDetails.getExtUid());
            userEntity.setEmail(userDetails.getEmail());
            userEntity.setSurname(userDetails.getSurname());
            userEntity.setName(userDetails.getName());
            userEntity.setIsActive(true);
            userEntity.setExtSys(userDetails.getExtSys());
            if (userDetails.getRoleNames() != null && !userDetails.getRoleNames().isEmpty()) {
                userEntity.setRoleList(userDetails.getRoleNames().stream().map(this::getOrCreateRole).filter(Objects::nonNull).collect(Collectors.toList()));
            }
            userRepository.save(userEntity);
        } else if (userEntity == null && !createUser) {
            throw new UserNotFoundOauthException("");
        } else if (updateUser) {
            userEntity.setIsActive(true);
            if (userDetails.getExtUid() != null) {
                userEntity.setExtUid(userDetails.getExtUid());
            }
            if (userDetails.getEmail() != null) {
                userEntity.setEmail(userDetails.getEmail());
            }
            if (userDetails.getPatronymic() != null) {
                userEntity.setPatronymic(userDetails.getPatronymic());
            }
            if (userDetails.getSurname() != null) {
                userEntity.setSurname(userDetails.getSurname());
            }
            if (userDetails.getName() != null) {
                userEntity.setName(userDetails.getName());
            }
            if (userDetails.getRoleNames() == null && updateRoles) {
                userEntity.getRoleList().clear();
            } else if (updateRoles) {
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

    private Role getRoleModel(String code) {
        return model(roleRepository.findOneByCode(code));
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

        if (entity.getRoleList() != null && !entity.getRoleList().isEmpty()) {
            model.setRoles(entity.getRoleList().stream().map(this::model).collect(Collectors.toList()));
        } else if (!defaultRoles.isEmpty()) {
            model.setRoles(defaultRoles.stream().map(this::getRoleModel).filter(Objects::nonNull).collect(Collectors.toList()));
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

    public UserDetailsServiceImpl setCreateUser(Boolean createUser) {
        this.createUser = createUser;
        return this;
    }

    public UserDetailsServiceImpl setDefaultRoles(List<String> defaultRoles) {
        this.defaultRoles = defaultRoles;
        return this;
    }

    public UserDetailsServiceImpl addDefaultRoles(String... role) {
        this.defaultRoles.addAll(Arrays.asList(role));
        return this;
    }

    public UserDetailsServiceImpl setUpdateUser(Boolean updateUser) {
        this.updateUser = updateUser;
        return this;
    }

    public UserDetailsServiceImpl setUpdateRoles(Boolean updateRoles) {
        this.updateRoles = updateRoles;
        return this;
    }
}
