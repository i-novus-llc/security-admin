package net.n2oapp.security.admin.service;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.security.admin.entity.PermissionEntity;
import net.n2oapp.security.admin.entity.RoleEntity;
import net.n2oapp.security.admin.model.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RolePageService implements CollectionPageService<RoleCriteria, Role> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public CollectionPage<Role> getCollectionPage(RoleCriteria criteria) {
        List<RoleEntity> roleEntities;
        if (criteria.getId() == null) {
             roleEntities = entityManager.createQuery("select r from net.n2oapp.security.admin.entity.RoleEntity r where "
                     + " (:code is null or lower(trim(r.code)) like :code) and (:name is null or lower(trim(r.name)) like :name) and "
                     + " (:description is null or lower(trim(r.description)) like :description)")
                    .setParameter("code", prepareForLike(criteria.getCode()))
                    .setParameter("name", prepareForLike(criteria.getName()))
                    .setParameter("description", prepareForLike(criteria.getDescription()))
                    .getResultList();
        } else {
            roleEntities =  Arrays.asList((RoleEntity) entityManager.createQuery("select r from net.n2oapp.security.admin.entity.RoleEntity r left join fetch r.permissionSet where r.id = :id")
                    .setParameter("id", criteria.getId()).getSingleResult());
        }
        List<Role> roles = roleEntities.stream().map(re -> convertEntity(re)).collect(Collectors.toList());
        return new CollectionPage<>(roles.size(), roles, criteria);
    }

    @Transactional
    public Role create(Role model) {
        RoleEntity role = entityManager.merge(convertModel(model));
        model.setId(role.getId());
        return model;
    }

    @Transactional
    public Role update(Role model) {
        entityManager.merge(convertModel(model));
        return model;
    }

    @Transactional
    public void delete(Integer id) {
        entityManager.remove(entityManager.find(RoleEntity.class, id));
    }

    private String prepareForLike(String value) {
        if (value == null) return null;
        return "%" + value.toLowerCase().trim() + "%";
    }

    private Role convertEntity(RoleEntity entity) {
        Role role = new Role();
        role.setId(entity.getId());
        role.setName(entity.getName());
        role.setCode(entity.getCode());
        role.setDescription(entity.getDescription());
        if (entity.getPermissionSet() != null) {
            role.setPermissionIds(entity.getPermissionSet().stream().map(p -> p.getId()).collect(Collectors.toSet()));
        }
        return role;
    }

    private RoleEntity convertModel(Role model) {
        RoleEntity role = new RoleEntity();
        role.setId(model.getId());
        role.setName(model.getName());
        role.setCode(model.getCode());
        role.setDescription(model.getDescription());
        if (model.getPermissionIds() != null) {
            Set<PermissionEntity> permissions = new HashSet<>();
            model.getPermissionIds().forEach(p -> {
                PermissionEntity permission = entityManager.find(PermissionEntity.class, p);
                if (permission != null) {
                    permissions.add(permission);
                }
            });
            role.setPermissionSet(permissions);
        }
        return role;
    }


}
