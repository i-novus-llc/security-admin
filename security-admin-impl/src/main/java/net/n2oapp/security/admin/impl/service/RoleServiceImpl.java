package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.specification.RoleSpecifications;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    private ModelMapper modelMapper;


    @Override
    public Role create(Role role) {
        checkRoleUniq(role.getId(), role.getName());
        return model(roleRepository.save(entity(role)));

    }

    @Override
    public Role update(Role role) {
        checkRoleUniq(role.getId(), role.getName());
        return model(roleRepository.save(entity(role)));
    }

    @Override
    public void delete(Integer id) {
        checkRoleExist(id);
        roleRepository.delete(id);
    }

    @Override
    public Role getById(Integer id) {
        RoleEntity roleEntity = roleRepository.findOne(id);
        return model(roleEntity);
    }

    @Override
    public Page<Role> findAll(RoleCriteria criteria) {
        Specification<RoleEntity> specification = new RoleSpecifications(criteria);
        Page<RoleEntity> all = roleRepository.findAll(specification, criteria);
        return all.map(this::model);
    }

    private RoleEntity entity(Role model) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        if (model == null) return null;
        RoleEntity entity = modelMapper.map(model, RoleEntity.class);
        entity.setPermissionList(model.getPermissionIds().stream().map(PermissionEntity::new).collect(Collectors.toList()));
        return entity;
    }

    private Role model(RoleEntity entity) {
        if (entity == null) return null;
        Role model = modelMapper.map(entity, Role.class);
        model.setPermissionIds(entity.getPermissionList().stream().map(PermissionEntity::getId).collect(Collectors.toList()));
        model.setPermissionNames(entity.getPermissionList().stream().map(PermissionEntity::getName).collect(Collectors.toList()));
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
            throw new IllegalArgumentException("Role with such name already exists");
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
            throw new IllegalAccessError("Deleting is not possible, since there are users with such role");
    }
}
