package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.impl.entity.AccountEntity;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.exception.UserNotFoundAuthenticationException;
import net.n2oapp.security.admin.impl.repository.AccountRepository;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Transactional
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    private final static String DEFAULT_ACCOUNT_PREFIX = "Базовый аккаунт пользователя ";

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected AccountRepository accountRepository;

    @Value("${access.keycloak.ignore-roles:offline_access,uma_authorization}")
    private String[] ignoreRoles;
    @Value("${access.permission.enabled}")
    private Boolean permissionEnabled;
    @Value("${access.email-as-username:false}")
    private Boolean emailAsUsername;

    private Boolean createUser = true;

    private List<String> defaultRoles = new ArrayList<>();

    @Override
    public User loadUserDetails(UserDetailsToken userDetails) {
        UserEntity userEntity = userRepository.findOneByUsernameIgnoreCase(Boolean.TRUE.equals(emailAsUsername) ? userDetails.getEmail() : userDetails.getUsername());
        if (isNull(userEntity) && !createUser)
            throw new UserNotFoundAuthenticationException("User " + userDetails.getName() + " " + userDetails.getSurname() + " doesn't registered in system");

        if (isNull(userEntity))
            userEntity = new UserEntity();
        userEntity.setUsername(Boolean.TRUE.equals(emailAsUsername) ? userDetails.getEmail() : userDetails.getUsername());
        userEntity.setEmail(userDetails.getEmail());
        userEntity.setSurname(userDetails.getSurname());
        userEntity.setPatronymic(userDetails.getPatronymic());
        userEntity.setName(userDetails.getName());
        userEntity.setIsActive(true);
        userRepository.save(userEntity);
        if (isNull(userEntity.getAccounts()) || userEntity.getAccounts().size() < 2) {
            AccountEntity accountEntity = CollectionUtils.firstElement(userEntity.getAccounts());
            if (isNull(accountEntity)) {
                accountEntity = new AccountEntity();
                accountEntity.setUser(userEntity);
                userEntity.setAccounts(List.of(accountEntity));
            }
            accountEntity.setName(DEFAULT_ACCOUNT_PREFIX + userEntity.getUsername());
            accountEntity.setUser(userEntity);
            accountEntity.setIsActive(true);
            accountEntity.setExternalSystem(userDetails.getExternalSystem());
            accountEntity.setExternalUid(userDetails.getExtUid());


            List<String> roleNamesCopy = new ArrayList<>(userDetails.getRoleNames());
            List<RoleEntity> roleForRemove = new ArrayList<>();
            if (isNull(accountEntity.getRoleList()))
                accountEntity.setRoleList(new ArrayList<>());
            for (RoleEntity r : accountEntity.getRoleList()) {
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
                accountEntity.getRoleList().remove(r);
            }
            for (String r : roleNamesCopy) {
                accountEntity.getRoleList().add(getOrCreateRole(r));
            }

            accountRepository.save(accountEntity);
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

        model.setExpirationDate(entity.getExpirationDate());

        return model;
    }

    protected Role model(RoleEntity entity) {
        if (isNull(entity)) return null;
        Role model = new Role();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        if (nonNull(entity.getSystem()))
            model.setSystem(new AppSystem(entity.getSystem().getCode()));
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
}
