package net.n2oapp.security.admin.impl.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public User loadUserDetails(UserDetailsToken userDetails) {
        UserEntity userEntity = userRepository.findOneByUsername(userDetails.getUsername());
        if (userEntity == null) {
            userEntity = new UserEntity();
            userEntity.setUsername(userDetails.getUsername());
            userEntity.setGuid(userDetails.getGuid() == null ? null : UUID.fromString(userDetails.getGuid()));
            userEntity.setEmail(userDetails.getEmail());
            userEntity.setSurname(userDetails.getSurname());
            userEntity.setName(userDetails.getName());
            userEntity.setIsActive(true);
            if (userDetails.getRoleNames() != null){
                userEntity.setRoleList(userDetails.getRoleNames().stream().map(this::getOrCreateRole).collect(Collectors.toList()));
            }
            userRepository.save(userEntity);
        } else {
            userEntity.setIsActive(true);
            if (userDetails.getGuid() != null) {
                userEntity.setGuid(userDetails.getGuid() == null ? null : UUID.fromString(userDetails.getGuid()));
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
                if (!roleForRemove.isEmpty()) {
                    for (RoleEntity r : roleForRemove) {
                        userEntity.getRoleList().remove(r);
                    }
                }
                if (!roleNamesCopy.isEmpty()) {
                    for(String r : roleNamesCopy) {
                        RoleEntity role = getOrCreateRole(r);
                        userEntity.getRoleList().add(role);
                    }
                }
            }
        }
        return model(userEntity);
    }

    private RoleEntity getOrCreateRole(String name) {
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
        model.setGuid(entity.getGuid() == null ? null : entity.getGuid().toString());
        model.setUsername(entity.getUsername());
        model.setName(entity.getName());
        model.setSurname(entity.getSurname());
        model.setPatronymic(entity.getPatronymic());
        model.setIsActive(entity.getIsActive());
        model.setEmail(entity.getEmail());
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
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        model.setParentId(entity.getParentId());
        return model;
    }

}
