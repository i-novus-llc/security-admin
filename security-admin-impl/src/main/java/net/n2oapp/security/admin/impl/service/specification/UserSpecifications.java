package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity_;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by otihonova on 02.11.2017.
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
        if (criteria.getName() != null)
            predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.name), criteria.getName()));
        if (criteria.getSurname() != null)
            predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.surname), criteria.getSurname()));
        if (criteria.getPatronymic() != null)
            predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.patronymic), criteria.getPatronymic()));
        if (criteria.getIsActive() != null) {
                predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.isActive), criteria.getIsActive()));
        }
        return predicate;
    }
}



