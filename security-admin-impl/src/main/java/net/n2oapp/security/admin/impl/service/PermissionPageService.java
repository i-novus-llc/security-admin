package net.n2oapp.security.admin.impl.service;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.security.admin.api.entity.PermissionEntity;
import net.n2oapp.security.admin.api.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionPageService implements CollectionPageService<PermissionCriteria, Permission> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public CollectionPage<Permission> getCollectionPage(PermissionCriteria criteria) {
        List<PermissionEntity> permissionEntities = entityManager.createQuery("select p from net.n2oapp.security.admin.entity.PermissionEntity p where (:id is null or p.id = :id) and "
                + " (:code is null or lower(trim(p.code)) like :code) and (:name is null or lower(trim(p.name)) like :name) ")
                .setParameter("id", criteria.getId())
                .setParameter("code", prepareForLike(criteria.getCode()))
                .setParameter("name", prepareForLike(criteria.getName()))
                .getResultList();
        List<Permission> permissions = permissionEntities.stream().map(re -> convertEntity(re)).collect(Collectors.toList());
        return new CollectionPage<>(permissions.size(), permissions, criteria);
    }

    private String prepareForLike(String value) {
        if (value == null) return null;
        return "%" + value.toLowerCase().trim() + "%";
    }

    private Permission convertEntity(PermissionEntity entity) {
        Permission permission = new Permission();
        permission.setId(entity.getId());
        permission.setName(entity.getName());
        permission.setCode(entity.getCode());
        permission.setParentId(entity.getParentId());
        return permission;
    }
}
