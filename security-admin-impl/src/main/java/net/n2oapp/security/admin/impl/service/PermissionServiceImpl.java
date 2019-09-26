package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.PermissionCriteria;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import net.n2oapp.security.admin.impl.service.specification.PermissionSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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