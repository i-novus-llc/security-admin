package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.audit.AuditService;
import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.OrganizationRepository;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.SystemRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.specification.RoleSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * Реализация сервиса управления ролями
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Value("${access.permission.enabled}")
    private Boolean permissionEnabled;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private SystemRepository systemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SsoUserRoleProvider provider;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private AuditService auditService;

    @Override
    public Role create(RoleForm role) {
        checkSystemCode(role.getSystemCode());
        checkRoleUnique(role);
        Role result = model(roleRepository.save(entity(role)));
        // если отсутствует код роли, то устанавливаем
        if (result.getCode() == null)
            result.setCode("ROLE_" + result.getId());
        Role providerResult = provider.createRole(result);
        if (providerResult != null) {
            if (result.getSystem() == null || result.getSystem().getCode() == null)
                result.setSystem(null);
            providerResult.setSystem(result.getSystem());
            result = providerResult;
            roleRepository.save(entity(result));
        }
        return audit("audit.roleCreate", result);
    }

    @Override
    public Role update(RoleForm role) {
        checkSystemCode(role.getSystemCode());
        checkRoleUnique(role);
        Role result = model(roleRepository.save(entity(role)));
        provider.updateRole(result);
        return audit("audit.roleUpdate", result);
    }

    @Override
    public void delete(Integer id) {
        RoleEntity entity = roleRepository.findById(id).orElseThrow(NotFoundException::new);
        checkRoleIsUsed(entity);
        Role role = model(entity);
        roleRepository.deleteById(id);
        if (role != null) {
            audit("audit.roleDelete", role);
            try {
                provider.deleteRole(role);
            } catch (UserException ex) {
                if (ex.getCause() instanceof HttpClientErrorException && ((HttpClientErrorException) ex.getCause()).getRawStatusCode() == 404)
                    log.warn(String.format("Role with id %d not found in keycloak", id), ex);
                else
                    throw ex;
            }
        }
    }

    @Override
    public Role getById(Integer id) {
        RoleEntity roleEntity = roleRepository.findById(id).orElseThrow(NotFoundException::new);
        return model(roleEntity);
    }

    @Override
    public Role getByIdWithSystem(Integer id) {
        if (id != null && id < 0)
            return modelSystemToRole(systemRepository.findOneByIntCode(-id).orElseThrow(NotFoundException::new));
        return getById(id);
    }

    @Override
    public Page<Role> findAll(RoleCriteria criteria) {
        Specification<RoleEntity> specification = new RoleSpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(new ArrayList<>());
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "code"));
        }
        if (Boolean.TRUE.equals(criteria.getGroupBySystem())) {
            return this.groupBySystem(specification, criteria);
        } else {
            Page<RoleEntity> all = roleRepository.findAll(specification, criteria);
            return all.map(this::model);
        }
    }

    private Page<Role> groupBySystem(Specification<RoleEntity> specification, RoleCriteria criteria) {
        List<RoleEntity> roles = roleRepository.findAll(specification, criteria).stream().collect(Collectors.toList());
        Set<SystemEntity> systems = new HashSet<>();
        List<Role> modelRoles = new ArrayList<>();
        for (int i = 0; i < roles.size(); ) {
            RoleEntity roleEntity = roles.get(i);
            SystemEntity system = roleEntity.getSystem();
            if (nonNull(system)) {
                if (!systems.contains(system)) {
                    systems.add(system);
                    modelRoles.add(modelSystemToRole(system));
                }
                Role role = model(roleEntity);
                role.getSystem().setCode(modelRoles.stream().filter(model -> model.getCode().equals(system.getCode())).findFirst().get().getId().toString());
                modelRoles.add(role);
                roles.remove(roleEntity);
            } else i++;
        }

        modelRoles.addAll(roles.stream().map(this::model).collect(Collectors.toList()));
        return new PageImpl<>(modelRoles);
    }

    private RoleEntity entity(RoleForm model) {
        if (model == null) return null;
        RoleEntity entity = new RoleEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setDescription(model.getDescription());
        if (nonNull(model.getUserLevel()))
            entity.setUserLevel(UserLevel.valueOf(model.getUserLevel()));
        if (model.getSystemCode() != null)
            entity.setSystem(new SystemEntity(model.getSystemCode()));

        if (model.getPermissions() != null) {
            entity.setPermissionList(model.getPermissions().stream().filter(s -> !s.startsWith("$")).map(PermissionEntity::new).collect(Collectors.toList()));
        }
        return entity;
    }

    private RoleEntity entity(Role model) {
        if (model == null) return null;
        RoleEntity entity = new RoleEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setUserLevel(model.getUserLevel());
        entity.setDescription(model.getDescription());
        if (model.getSystem() != null) entity.setSystem(new SystemEntity(model.getSystem().getCode()));
        if (model.getPermissions() != null) {
            entity.setPermissionList(model.getPermissions().stream().map(this::entity).collect(Collectors.toList()));
        }
        return entity;
    }

    private Role modelSystemToRole(SystemEntity entity) {
        if (entity == null) return null;
        Role role = new Role();
        role.setId(-entity.getIntCode());
        role.setName(entity.getName());
        role.setCode(entity.getCode());
        return role;
    }

    private Role model(RoleEntity entity) {
        if (entity == null) return null;
        Role model = new Role();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        model.setUserLevel(entity.getUserLevel());
        if (entity.getSystem() != null)
            model.setSystem(model(entity.getSystem()));
        model.setDescription(entity.getDescription());
        if (permissionEnabled && entity.getPermissionList() != null) {
            model.setPermissions(entity.getPermissionList().stream().map(this::model).collect(Collectors.toList()));
        }
        return model;
    }

    private Permission model(PermissionEntity entity) {
        if (entity == null) return null;
        Permission model = new Permission();
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        return model;
    }

    private PermissionEntity entity(Permission model) {
        if (model == null) return null;
        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setCode(model.getCode());
        permissionEntity.setName(model.getName());
        return permissionEntity;
    }

    private AppSystem model(SystemEntity entity) {
        if (entity == null) return null;
        AppSystem model = new AppSystem();
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        model.setDescription(entity.getDescription());
        return model;
    }

    private void checkSystemCode(String systemCode) {
        if (systemCode != null && !systemRepository.existsByCode(systemCode))
            throw new UserException("exception.systemNotExists");
    }

    /**
     * Валидация на уникальность названия и кода роли при изменении
     */
    private void checkRoleUnique(RoleForm role) {
        if (!roleRepository.checkRoleUnique(role.getId() == null ? -1 : role.getId(), role.getName(), role.getCode()))
            throw new UserException("exception.uniqueRole");
    }

    /**
     * Валидация на удаление ролей
     * Запрещено удалять роль, если существует пользователь, клиент, организация или тип аккаунта с такой ролью
     */
    private void checkRoleIsUsed(RoleEntity entity) {
        if (!entity.getAccountList().isEmpty())
            throw new UserException("exception.usernameWithSuchRoleExists");
        if (!entity.getAccountTypeRoleList().isEmpty())
            throw new UserException("exception.accountTypeWithSuchRoleExists");
        if (organizationRepository.countOrgsWithRoleId(entity.getId()) != 0)
            throw new UserException("exception.organizationWithSuchRoleExists");
    }

    private Role audit(String action, Role role) {
        auditService.audit(action, role, "" + role.getCode(), "audit.role");
        return role;
    }
}
