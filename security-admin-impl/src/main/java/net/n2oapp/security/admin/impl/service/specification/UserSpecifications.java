package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.impl.entity.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;


/**
 * Реализация фильтров для юзера
 */
public class UserSpecifications implements Specification<UserEntity> {

    private UserCriteria criteria;

    public UserSpecifications(UserCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        Predicate predicate = builder.and();
        if (criteria.getUsername() != null)
            predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.username), criteria.getUsername()));
        if (criteria.getFio() != null) {
            criteria.setFio(criteria.getFio().toLowerCase().replace(" ", ""));
            predicate = builder.and(predicate,
                    builder.or(
                            builder.like(builder.lower(builder.trim(root.get(UserEntity_.name))), criteria.getFio() + "%"),
                            builder.like(builder.lower(builder.trim(root.get(UserEntity_.patronymic))), criteria.getFio() + "%"),
                            builder.like(builder.concat(builder.coalesce(builder.lower(builder.trim(root.get(UserEntity_.name))), ""),
                                    builder.coalesce(builder.lower(builder.trim(root.get(UserEntity_.patronymic))), "")), criteria.getFio() + "%"),
                            builder.like(builder.concat(builder.coalesce(builder.lower(builder.trim(root.get(UserEntity_.surname))), ""),
                                    builder.concat(builder.coalesce(builder.lower(builder.trim(root.get(UserEntity_.name))), ""),
                                            builder.coalesce(builder.lower(builder.trim(root.get(UserEntity_.patronymic))), ""))), criteria.getFio() + "%")));
        }
        if (criteria.getIsActive() != null) {
            predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.isActive), criteria.getIsActive()));
        }
        if(criteria.getBank()!= null){
            predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.bank).get("id"), criteria.getBank()));
        }
        if (criteria.getRoleIds() != null && !criteria.getRoleIds().isEmpty()) {
            Subquery sub = criteriaQuery.subquery(Integer.class);
            Root subRoot = sub.from(RoleEntity.class);
            ListJoin<RoleEntity, UserEntity> subUsers = subRoot.join(RoleEntity_.userList);
            sub.select(subRoot.get(RoleEntity_.id));
            sub.where(builder.and(builder.equal(root.get(UserEntity_.id), subUsers.get(UserEntity_.id)),
                    subRoot.get(RoleEntity_.id).in(criteria.getRoleIds())));
            predicate = builder.and(predicate, builder.exists(sub));
        }
        return predicate;
    }
}



