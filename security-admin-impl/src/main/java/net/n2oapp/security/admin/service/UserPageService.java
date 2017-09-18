package net.n2oapp.security.admin.service;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.security.admin.entity.RoleEntity;
import net.n2oapp.security.admin.entity.UserEntity;
import net.n2oapp.security.admin.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserPageService implements CollectionPageService<UserCriteria, User> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public CollectionPage<User> getCollectionPage(UserCriteria criteria) {
        List<UserEntity> userEntities;
        if (criteria.getId() == null) {
            userEntities = entityManager.createQuery("select usr from net.n2oapp.security.admin.entity.UserEntity usr where "
                    + " (:username is null or lower(trim(usr.username)) like :username) and (:name is null or lower(trim(usr.name)) like :name) and "
                    + " (:surname is null or lower(trim(usr.surname)) like :surname) and (:patronymic is null or lower(trim(usr.patronymic)) like :patronymic) and "
                    + " (:isActive is null or usr.isActive = :isActive )")
                    .setParameter("username", prepareForLike(criteria.getUsername()))
                    .setParameter("surname", prepareForLike(criteria.getSurname()))
                    .setParameter("name", prepareForLike(criteria.getName()))
                    .setParameter("patronymic", prepareForLike(criteria.getPatronymic()))
                    .setParameter("isActive", criteria.isActive())
                    .getResultList();
        } else {
            userEntities = Arrays.asList((UserEntity) entityManager.createQuery("select usr from net.n2oapp.security.admin.entity.UserEntity usr left join fetch usr.roleSet where usr.id = :id")
                    .setParameter("id", criteria.getId()).getSingleResult());
        }
        List<User> users = userEntities.stream().map(u -> convertEntity(u)).collect(Collectors.toList());
        return new CollectionPage<>(users.size(), users, criteria);
    }

    @Transactional
    public User create(User model) {
        UserEntity user = entityManager.merge(convertModel(model));
        model.setId(user.getId());
        return model;
    }

    @Transactional
    public User update(User model) {
        entityManager.merge(convertModel(model));
        return model;
    }

    @Transactional
    public void delete(Integer id) {
        entityManager.remove(entityManager.find(UserEntity.class, id));
    }

    public void changeUserActive(Integer id, Boolean isActive) {
       throw new UnsupportedOperationException();
    }

    private String prepareForLike(String value) {
        if (value == null) return null;
        return "%" + value.toLowerCase().trim() + "%";
    }

    private User convertEntity(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setSurname(entity.getSurname());
        user.setName(entity.getName());
        user.setPatronymic(entity.getPatronymic());
        user.setPassword(entity.getPassword());
        user.setIsActive(entity.getIsActive());
        if (entity.getRoleSet() != null) {
            user.setRoleIds(entity.getRoleSet().stream().map(r -> r.getId()).collect(Collectors.toSet()));
        }
        return user;
    }

    private UserEntity convertModel(User model) {
        UserEntity user = new UserEntity();
        user.setId(model.getId());
        user.setUsername(model.getUsername());
        user.setSurname(model.getSurname());
        user.setName(model.getName());
        user.setPatronymic(model.getPatronymic());
        user.setPassword(model.getPassword());
        user.setIsActive(model.getIsActive());
        if (model.getRoleIds() != null) {
            Set<RoleEntity> roles = new HashSet<>();
            model.getRoleIds().forEach(r -> {
                RoleEntity role = entityManager.find(RoleEntity.class, r);
                if (role != null) {
                    roles.add(role);
                }
            });
            user.setRoleSet(roles);
        }
        return user;
    }

}
