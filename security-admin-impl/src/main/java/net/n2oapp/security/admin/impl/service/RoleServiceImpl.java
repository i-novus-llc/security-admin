package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;;
import net.n2oapp.security.admin.impl.service.specification.RoleSpecifications;
import org.modelmapper.ModelMapper;
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
    private ModelMapper modelMapper;

    @Override
    public Integer create(Role role) {
        return roleRepository.save(convertToRoleEntity(role)).getId();
    }

    @Override
    public Integer update(Role role) {
        return roleRepository.save(convertToRoleEntity(role)).getId();
    }

    @Override
    public void delete(Integer id) {
        roleRepository.delete(id);

    }

    @Override
    public Role getById(Integer id) {
        RoleEntity roleEntity=roleRepository.findOne(id);
        return convertToRole(roleEntity);
    }

    @Override
    public Page<Role> findAll(RoleCriteria criteria) {
        final Specification<RoleEntity> specification = new RoleSpecifications(criteria);
        final Page<RoleEntity> all = roleRepository.findAll(specification, criteria);
        return all.map(this::convertToRole);
    }

    private RoleEntity convertToRoleEntity(Role role){
        RoleEntity roleEntity =modelMapper.map(role,RoleEntity.class);
        roleEntity.setPermissionSet(role.getPermissionIds().stream().map(PermissionEntity::new).collect(Collectors.toSet()));
        return roleEntity;
    }
    private Role convertToRole (RoleEntity roleEntity){
        if (roleEntity == null) return null;
        Role role = modelMapper.map(roleEntity, Role.class);
        role.setPermissionIds(roleEntity.getPermissionSet().stream().map(PermissionEntity::getId).collect(Collectors.toSet()));
        return role;

    }
}
