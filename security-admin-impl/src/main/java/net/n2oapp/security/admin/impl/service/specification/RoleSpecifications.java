package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.impl.entity.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Arrays;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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
        if (nonNull(criteria.getName()))
            predicate = builder.and(predicate, builder.like(builder.lower(root.get(RoleEntity_.name)), "%" + criteria.getName().toLowerCase() + "%"));
        if (nonNull(criteria.getDescription()))
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
            predicate = builder.and(predicate, systemPredicate);
        }

        if (nonNull(criteria.getUserLevel()) && (isNull(criteria.getForForm()) || Boolean.FALSE.equals(criteria.getForForm()))) {
            String userLevel = criteria.getUserLevel().toUpperCase();
            // если userLevel не существует, то возвращаем false-предикат
            if (Arrays.stream(UserLevel.values()).map(UserLevel::getName).noneMatch(u -> u.equals(userLevel))) {
                return builder.disjunction();
            }
            if (UserLevel.NOT_SET.getName().equals(userLevel)) {
                predicate = builder.and(predicate, builder.isNull(root.get(RoleEntity_.userLevel)));
            } else {
                predicate = builder.and(predicate, builder.equal(root.get(RoleEntity_.userLevel),
                        UserLevel.valueOf(userLevel)));
            }
        }

        if (Boolean.TRUE.equals(criteria.getForForm()) && nonNull(criteria.getUserLevel())) {
            predicate = builder.and(predicate, builder.or(builder.equal(root.get(RoleEntity_.userLevel),
                    UserLevel.valueOf(criteria.getUserLevel())), builder.isNull(root.get(RoleEntity_.USER_LEVEL))));
        } else if (Boolean.TRUE.equals(criteria.getForForm())) {
            predicate = builder.and(predicate, builder.isNull(root.get(RoleEntity_.USER_LEVEL)));
        }


        if (nonNull(criteria.getOrgRoles()) && Boolean.TRUE.equals(criteria.getFilterByOrgRoles())) {
            if (criteria.getOrgRoles().isEmpty())
                return builder.disjunction();
            predicate = builder.and(predicate, builder.and(root.get(RoleEntity_.ID).in(criteria.getOrgRoles())));
        }
        return predicate;
    }
}




















