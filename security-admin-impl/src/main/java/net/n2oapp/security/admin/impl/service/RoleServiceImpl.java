package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.impl.audit.AuditHelper;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.specification.RoleSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * Реализация сервиса управления ролями
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SsoUserRoleProvider provider;
    @Autowired
    private AuditHelper audit;

    @Override
    public Role create(RoleForm role) {
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
        checkRoleUnique(role);
        Role result = model(roleRepository.save(entity(role)));
        provider.updateRole(result);
        return audit("audit.roleUpdate", result);
    }

    @Override
    public void delete(Integer id) {
        checkRoleIsUsed(id);
        Role role = model(roleRepository.findById(id).orElseThrow(NotFoundException::new));
        roleRepository.deleteById(id);
        if (role != null) {
            audit("audit.roleDelete", role);
            provider.deleteRole(role);
        }
    }

    @Override
    public Role getById(Integer id) {
        RoleEntity roleEntity = roleRepository.findById(id).orElseThrow(NotFoundException::new);
        return model(roleEntity);
    }

    @Override
    public Page<Role> findAll(RoleCriteria criteria) {
        Specification<RoleEntity> specification = new RoleSpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "code")));
        } else {
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
        int dummyRoleId = -1;
        for (int i = 0; i < roles.size(); ) {
            RoleEntity roleEntity = roles.get(i);
            if (nonNull(roleEntity.getSystemCode())) {
                if (!systems.contains(roleEntity.getSystemCode())) {
                    systems.add(roleEntity.getSystemCode());
                    Role dummyRole = new Role();
                    dummyRole.setId(dummyRoleId);
                    dummyRole.setName(roleEntity.getSystemCode().getName());
                    dummyRole.setCode(roleEntity.getSystemCode().getCode());
                    modelRoles.add(dummyRole);
                    dummyRoleId--;
                }
                Role role = model(roleEntity);
                role.getSystem().setCode(modelRoles.stream().filter(model -> model.getCode().equals(roleEntity.getSystemCode().getCode())).findFirst().get().getId().toString());
                modelRoles.add(role);
                roles.remove(roleEntity);
            } else i++;
        }

        modelRoles.addAll(roles.stream().map(this::model).collect(Collectors.toList()));
        return new PageImpl<>(modelRoles);
    }

    @Override
    public Integer countUsersWithRole(Integer roleId) {
        return userRepository.countUsersWithRoleId(roleId);
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
            entity.setSystemCode(new SystemEntity(model.getSystemCode()));

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
        if (model.getSystem() != null) entity.setSystemCode(new SystemEntity(model.getSystem().getCode()));
        if (model.getPermissions() != null) {
            entity.setPermissionList(model.getPermissions().stream().map(this::entity).collect(Collectors.toList()));
        }
        return entity;
    }

    private Role model(RoleEntity entity) {
        if (entity == null) return null;
        Role model = new Role();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        model.setUserLevel(entity.getUserLevel());
        if (entity.getSystemCode() != null)
            model.setSystem(model(entity.getSystemCode()));
        model.setDescription(entity.getDescription());
        if (entity.getPermissionList() != null) {
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

    private PermissionEntity entity(Permission entity) {
        if (entity == null) return null;
        PermissionEntity model = new PermissionEntity();
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        return model;
    }

    private AppSystem model(SystemEntity entity) {
        if (entity == null) return null;
        AppSystem model = new AppSystem();
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        model.setDescription(entity.getDescription());
        return model;
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
     * Запрещено удалять роль, если существует пользователь с такой ролью
     */
    private void checkRoleIsUsed(Integer roleId) {
        if (userRepository.countUsersWithRoleId(roleId) != 0)
            throw new UserException("exception.usernameWithSuchRoleExists");
    }

    private Role audit(String action, Role role) {
        audit.audit(action, role, "" + role.getCode(), role.getName());
        return role;
    }
}
