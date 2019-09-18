package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.impl.entity.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * Реализация фильтров для ролей пользователя
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
            predicate = builder.and(predicate, builder.like(root.get(RoleEntity_.description),
                    criteria.getDescription() + "%"));
        if (criteria.getPermissionCodes() != null && !criteria.getPermissionCodes().isEmpty()) {
            Subquery sub = criteriaQuery.subquery(Integer.class);
            Root subRoot = sub.from(PermissionEntity.class);
            ListJoin<PermissionEntity, RoleEntity> subRoles = subRoot.join(PermissionEntity_.roleList);
            sub.select(subRoot.get(PermissionEntity_.code));
            sub.where(builder.and(builder.equal(root.get(RoleEntity_.id), subRoles.get(RoleEntity_.id)),
                    subRoot.get(PermissionEntity_.code).in(criteria.getPermissionCodes())));
            predicate = builder.and(predicate, builder.exists(sub));
        }
        if (criteria.getSystemCodes() != null && !criteria.getSystemCodes().isEmpty()) {
            Predicate systemPredicate = builder.or();
            for (String system : criteria.getSystemCodes()) {
                systemPredicate = builder.or(systemPredicate, builder.equal(root.get(RoleEntity_.systemCode).get(SystemEntity_.CODE), system));
            }
            return builder.and(predicate, systemPredicate);
        }
        return predicate;
    }
}




















