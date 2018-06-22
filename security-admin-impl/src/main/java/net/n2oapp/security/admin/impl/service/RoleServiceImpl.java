package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.specification.RoleSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

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

    @Override
    public Role create(RoleForm role) {
        checkRoleUniq(role.getId(), role.getName());
        Role result = model(roleRepository.save(entity(role)));
        Role providerResult = provider.createRole(result);
        if (providerResult != null) {
            result = providerResult;
            roleRepository.save(entity(result));
        }
        return result;
    }

    @Override
    public Role update(RoleForm role) {
        checkRoleUniq(role.getId(), role.getName());
        Role result = model(roleRepository.save(entity(role)));
        provider.updateRole(result);
        return result;
    }

    @Override
    public void delete(Integer id) {
        checkRoleExist(id);
        RoleEntity roleEntity = roleRepository.findOne(id);
        roleRepository.delete(id);
        provider.deleteRole(model(roleEntity));
    }

    @Override
    public Role getById(Integer id) {
        RoleEntity roleEntity = roleRepository.findOne(id);
        return model(roleEntity);
    }

    @Override
    public Page<Role> findAll(RoleCriteria criteria) {
        Specification<RoleEntity> specification = new RoleSpecifications(criteria);
        criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "id"));
        Page<RoleEntity> all = roleRepository.findAll(specification, criteria);
        return all.map(this::model);
    }

    private RoleEntity entity(RoleForm model) {
        if (model == null) return null;
        RoleEntity entity = new RoleEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setDescription(model.getDescription());
        if (model.getPermissions() != null) {
            entity.setPermissionList(model.getPermissions().stream().map(PermissionEntity::new).collect(Collectors.toList()));
        }
        return entity;
    }

    private RoleEntity entity(Role model) {
        if (model == null) return null;
        RoleEntity entity = new RoleEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setDescription(model.getDescription());
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
        model.setDescription(entity.getDescription());
        if (entity.getPermissionList() != null) {
            model.setPermissions(entity.getPermissionList().stream().map(this::model).collect(Collectors.toList()));
        }
        return model;

    }

    private Permission model(PermissionEntity entity) {
        if (entity == null) return null;
        Permission model = new Permission();
        model.setId(entity.getId());
        model.setName(entity.getName());
        return model;
    }

    private PermissionEntity entity(Permission entity) {
        if (entity == null) return null;
        PermissionEntity model = new PermissionEntity();
        model.setId(entity.getId());
        model.setName(entity.getName());
        return model;
    }


    /**
     * Валидация на уникальность названия роли при изменении
     */
    private Boolean checkRoleUniq(Integer id, String name) {
        RoleEntity roleEntity = roleRepository.findOneByName(name);
        Boolean result = id == null ? roleEntity == null : ((roleEntity == null) || (roleEntity.getId().equals(id)));
        if (result) {
            return true;
        } else {
            throw new UserException("exception.uniqueRole");
        }
    }

    /**
     * Валидация на удаление ролей
     * Запрещено удалять роль, если существует пользователь с такой ролью
     */
    private boolean checkRoleExist(Integer roleId) {
        if (userRepository.countUsersWithRoleId(roleId) == 0)
            return true;
        else
            throw new UserException("exception.usernameWithSuchRoleExists");
    }
}