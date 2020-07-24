package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.exception.UserNotFoundAuthenticationException;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Transactional
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Value("${access.keycloak.ignore-roles:offline_access,uma_authorization}")
    private String[] ignoreRoles;

    @Value("${access.permission.enabled}")
    private Boolean permissionEnabled;

    @Value("${access.email-as-username}")
    private Boolean emailAsUsername;

    private Boolean createUser = true;

    private List<String> defaultRoles = new ArrayList<>();

    private Boolean updateUser = true;

    private Boolean updateRoles = true;

    @Override
    public User loadUserDetails(UserDetailsToken userDetails) {
        UserEntity userEntity = userRepository.findOneByUsernameIgnoreCase(emailAsUsername ? userDetails.getEmail() : userDetails.getUsername());
        if (isNull(userEntity) && createUser) {
            userEntity = new UserEntity();
            userEntity.setUsername(emailAsUsername ? userDetails.getEmail() : userDetails.getUsername());
            userEntity.setExtUid(userDetails.getExtUid());
            userEntity.setEmail(userDetails.getEmail());
            userEntity.setSurname(userDetails.getSurname());
            userEntity.setName(userDetails.getName());
            userEntity.setIsActive(true);
            userEntity.setExtSys(userDetails.getExternalSystem());
            if (nonNull(userDetails.getRoleNames()) && !userDetails.getRoleNames().isEmpty()) {
                userEntity.setRoleList(userDetails.getRoleNames().stream().map(this::getOrCreateRole).filter(Objects::nonNull).collect(Collectors.toList()));
            }
            userRepository.save(userEntity);
        } else if (isNull(userEntity) && !createUser) {
            throw new UserNotFoundAuthenticationException("User " + userDetails.getName() + " " + userDetails.getSurname() + " doesn't registered in system");
        } else if (updateUser) {
            userEntity.setIsActive(true);
            if (nonNull(userDetails.getExtUid())) {
                userEntity.setExtUid(userDetails.getExtUid());
            }
            if (nonNull(userDetails.getEmail())) {
                userEntity.setEmail(userDetails.getEmail());
            }
            if (nonNull(userDetails.getPatronymic())) {
                userEntity.setPatronymic(userDetails.getPatronymic());
            }
            if (nonNull(userDetails.getSurname())) {
                userEntity.setSurname(userDetails.getSurname());
            }
            if (nonNull(userDetails.getName())) {
                userEntity.setName(userDetails.getName());
            }
            if (isNull(userDetails.getRoleNames()) && updateRoles) {
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
        if (isNull(roleEntity)) {
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
        if (isNull(entity)) return null;
        User model = new User();
        model.setId(entity.getId());
        model.setUsername(entity.getUsername());
        model.setName(entity.getName());
        model.setSurname(entity.getSurname());
        model.setPatronymic(entity.getPatronymic());
        model.setIsActive(entity.getIsActive());
        model.setEmail(entity.getEmail());
        StringBuilder builder = new StringBuilder();
        if (nonNull(entity.getSurname())) {
            builder.append(entity.getSurname()).append(" ");
        }
        if (nonNull(entity.getName())) {
            builder.append(entity.getName()).append(" ");
        }
        if (nonNull(entity.getPatronymic())) {
            builder.append(entity.getPatronymic());
        }
        model.setFio(builder.toString());

        if (nonNull(entity.getRoleList()) && !entity.getRoleList().isEmpty()) {
            model.setRoles(entity.getRoleList().stream().map(this::model).collect(Collectors.toList()));
        } else if (!defaultRoles.isEmpty()) {
            model.setRoles(defaultRoles.stream().map(this::getRoleModel).filter(Objects::nonNull).collect(Collectors.toList()));
        }


        if (nonNull(entity.getDepartment())) {
            Department d = new Department();
            d.setId(entity.getDepartment().getId());
            d.setCode(entity.getDepartment().getCode());
            d.setName(entity.getDepartment().getName());
            model.setDepartment(d);
        }

        if (nonNull(entity.getRegion())) {
            Region r = new Region();
            r.setId(entity.getRegion().getId());
            r.setCode(entity.getRegion().getCode());
            r.setOkato(entity.getRegion().getOkato());
            r.setName(entity.getRegion().getName());
            model.setRegion(r);
        }

        if (nonNull(entity.getOrganization())) {
            Organization o = new Organization();
            o.setId(entity.getOrganization().getId());
            o.setCode(entity.getOrganization().getCode());
            o.setFullName(entity.getOrganization().getFullName());
            o.setOgrn(entity.getOrganization().getOgrn());
            o.setOkpo(entity.getOrganization().getOkpo());
            o.setShortName(entity.getOrganization().getShortName());
            model.setOrganization(o);
        }

        model.setUserLevel(entity.getUserLevel());
        return model;
    }

    protected Role model(RoleEntity entity) {
        if (isNull(entity)) return null;
        Role model = new Role();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        if (nonNull(entity.getSystemCode()))
            model.setSystem(new AppSystem(entity.getSystemCode().getCode()));
        if (permissionEnabled && nonNull(entity.getPermissionList())) {
            model.setPermissions(entity.getPermissionList().stream().map(this::model).collect(Collectors.toList()));
        }
        return model;
    }

    private Permission model(PermissionEntity entity) {
        if (isNull(entity)) return null;
        Permission model = new Permission();
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        if (nonNull(entity.getSystemCode()))
            model.setSystem(new AppSystem(entity.getSystemCode().getCode()));
        if (nonNull(entity.getParentPermission()))
            model.setParent(model(entity.getParentPermission()));
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
