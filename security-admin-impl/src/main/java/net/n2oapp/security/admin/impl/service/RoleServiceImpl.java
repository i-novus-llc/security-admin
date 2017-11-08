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

import java.util.stream.Collectors;

/**
 * Created by otihonova on 31.10.2017.
 */
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
        return roleRepository.save(convertToRoleEntity(role)).getId(); //TODO:проверить
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
        RoleEntity roleEntity =modelMapper.map(role,RoleEntity.class); //TODO:возникнет проблема с представлением полей
        roleEntity.setPermissionSet(role.getPermissionIds().stream().map(PermissionEntity::new).collect(Collectors.toSet()));
        return roleEntity;
    }
    private Role convertToRole (RoleEntity roleEntity){
        Role role = modelMapper.map(roleEntity, Role.class); //TODO:возникнет проблема с представлением полей
        role.setPermissionIds(roleEntity.getPermissionSet().stream().map(PermissionEntity::getId).collect(Collectors.toSet()));
        return role;

    }
}
