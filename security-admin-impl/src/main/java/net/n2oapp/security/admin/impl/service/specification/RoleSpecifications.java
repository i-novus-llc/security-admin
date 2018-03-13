package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity_;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by otihonova on 07.11.2017.
 */
public class RoleSpecifications implements Specification<RoleEntity> {
    private RoleCriteria criteria;

    public RoleSpecifications(RoleCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<RoleEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        Predicate predicate = builder.and();
        if (criteria.getName() != null)
            predicate = builder.and(predicate, builder.like(root.get(RoleEntity_.name), criteria.getName() + "%"));
        if (criteria.getDescription() != null)
            predicate = builder.and(predicate, builder.like(root.get(RoleEntity_.description), criteria.getDescription() + "%"));
        return predicate;
    }
}




















