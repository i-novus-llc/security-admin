package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.PermissionCriteria;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import net.n2oapp.security.admin.impl.service.specification.PermissionSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Сервис прав доступа
 */
@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public Permission create(Permission permission) {
        return model(permissionRepository.save(entity(permission)));
    }

    @Override
    public Permission update(Permission permission) {
        return model(permissionRepository.save(entity(permission)));
    }

    @Override
    public void delete(String code) {
        permissionRepository.deleteById(code);
    }

    @Override
    public Permission getByCode(String code) {
        PermissionEntity permissionEntity = permissionRepository.findById(code).get();
        return model(permissionEntity);
    }

    @Override
    public List<Permission> getAll(PermissionCriteria criteria) {
        Specification<PermissionEntity> specification = new PermissionSpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "code")));
        } else {
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "code"));
        }
        return permissionRepository.findAll(specification, criteria).stream().map(this::model).collect(Collectors.toList());
    }

    @Override
    public List<Permission> getAllByParentCode(String parentCode) {
        return permissionRepository.findByParentCode(parentCode).stream().map(this::model).collect(Collectors.toList());
    }

    @Override
    public List<Permission> getAllByParentIdIsNull() {
        return permissionRepository.findByParentCodeIsNull().stream().map(this::model).collect(Collectors.toList());
    }

    @Override
    public List<Permission> getAllWithSystem(PermissionCriteria criteria) {
        Specification<PermissionEntity> specification = new PermissionSpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "code")));
        } else {
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "code"));
        }

        List<PermissionEntity> permissions = permissionRepository.findAll(specification, criteria).stream().collect(Collectors.toList());
        Set<SystemEntity> systems = new HashSet<>();
        List<Permission> modelPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.size(); i++) {
            PermissionEntity permission = permissions.get(i);
            if (isNull(permission.getParentCode())
                    && nonNull(permission.getSystemCode())) {
                Permission temp = model(permission);
                temp.setParentCode("$" + permission.getSystemCode().getCode());
                modelPermissions.add(temp);
                permissions.remove(permission);
                i--;
                if (!systems.contains(permission.getSystemCode())) {
                    systems.add(permission.getSystemCode());
                    Permission pseudoPermission = new Permission();
                    pseudoPermission.setName(permission.getSystemCode().getName());
                    pseudoPermission.setCode("$" + permission.getSystemCode().getCode());
                    pseudoPermission.setHasChildren(true);
                    modelPermissions.add(pseudoPermission);
                }
            }
        }
        modelPermissions.addAll(permissions.stream().map(this::model).collect(Collectors.toList()));
        return modelPermissions;
    }

    private PermissionEntity entity(Permission model) {
        if (model == null) return null;
        PermissionEntity entity = new PermissionEntity();
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setParentCode(model.getParentCode());
        if (model.getSystemCode() != null)
            entity.setSystemCode(new SystemEntity(model.getSystemCode()));
        return entity;
    }

    private Permission model(PermissionEntity entity) {
        if (entity == null) return null;
        Permission model = new Permission();
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        model.setParentCode(entity.getParentCode());
        model.setHasChildren(entity.getHasChildren());
        if (entity.getSystemCode() != null)
            model.setSystemCode(entity.getSystemCode().getCode());
        return model;
    }
}