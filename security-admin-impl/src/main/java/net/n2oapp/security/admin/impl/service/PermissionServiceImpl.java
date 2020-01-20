package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.PermissionCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.PermissionUpdateForm;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import net.n2oapp.security.admin.impl.service.specification.PermissionSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
        checkPermissionCodeUnique(permission.getCode());
        return model(permissionRepository.save(entity(permission)));
    }

    @Override
    public Permission update(PermissionUpdateForm permission) {
        PermissionEntity permissionForUpdate = permissionRepository.getOne(permission.getCode());
        permissionForUpdate.setName(permission.getName());
        if (permission.getParent() != null && permission.getParent().getCode() != null) {
            checkParent(permission);
            permissionForUpdate.setParentPermission(new PermissionEntity(permission.getParent().getCode()));
        } else
            permissionForUpdate.setParentPermission(null);
        return model(permissionRepository.save(permissionForUpdate));
    }

    @Override
    public void delete(String code) {
        PermissionEntity permissionEntity = permissionRepository.findById(code).get();
        checkRolesWithSuchPermission(permissionEntity);
        if (permissionEntity.getHasChildren())
            throw new UserException("exception.permissionHasChildren");
        permissionRepository.deleteById(code);
    }

    @Override
    public Permission getByCode(String code) {
        PermissionEntity permissionEntity = permissionRepository.findById(code).get();
        return model(permissionEntity);
    }

    @Override
    public Page<Permission> getAll(PermissionCriteria criteria) {
        Specification<PermissionEntity> specification = new PermissionSpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "code")));
        } else {
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "code"));
        }

        if (criteria.getWithoutParent() != null && criteria.getWithoutParent()) {
            return getAllWithoutAncestor(specification, criteria);
        }
        Page<PermissionEntity> permissionEntityPage = permissionRepository.findAll(specification, criteria);
        if (criteria.getWithSystem() != null && criteria.getWithSystem()) {
            return addPermissionFromSystem(permissionEntityPage);
        }
        return permissionEntityPage.map(this::model);
    }

    @Override
    public List<Permission> getAllByParentCode(String parentCode) {
        return permissionRepository.findByParentPermission(new PermissionEntity(parentCode)).stream().map(this::model).collect(Collectors.toList());
    }

    private Page<Permission> addPermissionFromSystem(Page<PermissionEntity> permissionEntityPage) {
        List<PermissionEntity> permissions = permissionEntityPage.stream().collect(Collectors.toList());
        List<Permission> modelPermissions = new ArrayList<>();
        Set<SystemEntity> systems = new HashSet<>();
        for (int i = 0; i < permissions.size(); i++) {
            PermissionEntity permission = permissions.get(i);
            if (isNull(permission.getParentPermission())
                    && nonNull(permission.getSystemCode())) {
                Permission temp = model(permission);
                temp.setParent(new Permission("$" + permission.getSystemCode().getCode()));
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
        return new PageImpl<>(modelPermissions);
    }

    private Page<Permission> getAllWithoutAncestor(Specification<PermissionEntity> specification, PermissionCriteria criteria) {
        List<PermissionEntity> permissions = permissionRepository.findAll(specification);
        List<PermissionEntity> checkForRemove = new ArrayList<>();
        List<PermissionEntity> forRemove = new ArrayList<>();
        for (PermissionEntity permission : permissions) {
            checkForRemove.clear();
            while (permission.getParentPermission() != null) {
                checkForRemove.add(permission);
                if (criteria.getCode().equals(permission.getParentPermission().getCode()))
                    forRemove.addAll(checkForRemove);
                permission = permission.getParentPermission();
            }
        }
        permissions.removeAll(forRemove);
        return new PageImpl<>(permissions.stream().map(this::model).collect(Collectors.toList()));
    }

    private PermissionEntity entity(Permission model) {
        if (model == null) return null;
        PermissionEntity entity = new PermissionEntity();
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setUserLevel(model.getUserLevel());
        if (model.getParent() != null && model.getParent().getCode() != null)
            entity.setParentPermission(new PermissionEntity(model.getParent().getCode()));
        if (model.getSystem() != null && model.getSystem().getCode() != null)
            entity.setSystemCode(new SystemEntity(model.getSystem().getCode()));
        return entity;
    }

    private Permission model(PermissionEntity entity) {
        if (entity == null) return null;
        Permission model = new Permission();
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        model.setHasChildren(entity.getHasChildren());
        model.setUserLevel(entity.getUserLevel());
        if (entity.getParentPermission() != null) {
            model.setParent(model(entity.getParentPermission()));
        }
        if (entity.getSystemCode() != null) {
            model.setSystem(new AppSystem(entity.getSystemCode().getCode()));
            model.getSystem().setName(entity.getSystemCode().getName());
        }
        return model;
    }

    /**
     * Валидация на создание права доступа
     * Запрещено создавать право доступа, если существует право доступа с таким кодом
     */
    private void checkPermissionCodeUnique(String code) {
        Optional<PermissionEntity> permissionEntity = permissionRepository.findById(code);
        if (permissionEntity.isPresent() && permissionEntity.get().getCode() != null)
            throw new UserException("exception.uniquePermissionCode");
    }

    /**
     * Валидация на удаление права доступа
     * Запрещено удалять право доступа, если существует роль или система с данным правом доступа
     */
    private void checkRolesWithSuchPermission(PermissionEntity permissionEntity) {
        if (!permissionEntity.getRoleList().isEmpty())
            throw new UserException("exception.roleOrSystemWithSuchPermissionExists");
    }

    /**
     * Право доступа не можеть быть родителем самого себя
     * и не может быть родителем родительского права доступа
     */
    private void checkParent(PermissionUpdateForm permission) {
        if (permission.getParent() != null && permission.getParent().getCode() != null) {
            if (permission.getCode().equals(permission.getParent().getCode()))
                throw new UserException("exception.selfParent");

            PermissionEntity parentPermission = permissionRepository.findById(permission.getParent().getCode()).get();
            while (parentPermission != null) {
                if (permission.getCode().equals(parentPermission.getCode()))
                    throw new UserException("exception.cyclicParent");
                parentPermission = parentPermission.getParentPermission();
            }
        }
    }
}