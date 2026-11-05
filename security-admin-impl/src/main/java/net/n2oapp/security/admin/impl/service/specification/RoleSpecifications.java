package net.n2oapp.security.admin.impl.service.specification;

import jakarta.persistence.criteria.*;
import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.impl.entity.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;

import static java.util.Objects.nonNull;

/**
 * Реализация фильтров для ролей пользователя
 */
public class RoleSpecifications implements Specification<RoleEntity> {
    private transient RoleCriteria criteria;

    public RoleSpecifications(RoleCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<RoleEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        Predicate predicate = builder.and();
        if (nonNull(criteria.getName()))
            predicate = builder.and(predicate, builder.like(builder.lower(root.get(RoleEntity_.name)), "%" + criteria.getName().toLowerCase() + "%"));
        if (nonNull(criteria.getDescription()))
            predicate = builder.and(predicate, builder.like(builder.lower(root.get(RoleEntity_.description)),
                    "%" + criteria.getDescription().toLowerCase() + "%"));
        if (!CollectionUtils.isEmpty(criteria.getPermissionCodes()))
            predicate = builder.and(predicate, permissionCodesPredicate(root, criteriaQuery, builder));
        if (!CollectionUtils.isEmpty(criteria.getSystemCodes()))
            predicate = builder.and(predicate, systemCodesPredicate(root, builder));

        if (nonNull(criteria.getUserLevel()) && !Boolean.TRUE.equals(criteria.getForForm())) {
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

    private Predicate permissionCodesPredicate(Root<RoleEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        Subquery<String> sub = criteriaQuery.subquery(String.class);
        Root<PermissionEntity> subRoot = sub.from(PermissionEntity.class);
        ListJoin<PermissionEntity, RoleEntity> subRoles = subRoot.join(PermissionEntity_.roleList);
        sub.select(subRoot.get(PermissionEntity_.code));
        sub.where(builder.and(builder.equal(root.get(RoleEntity_.id), subRoles.get(RoleEntity_.id)),
                subRoot.get(PermissionEntity_.code).in(criteria.getPermissionCodes())));
        return builder.exists(sub);
    }

    private Predicate systemCodesPredicate(Root<RoleEntity> root, CriteriaBuilder builder) {
        Predicate systemPredicate = builder.or();
        for (String system : criteria.getSystemCodes()) {
            systemPredicate = builder.or(systemPredicate, builder.equal(root.get(RoleEntity_.system).get(SystemEntity_.CODE), system));
        }
        return systemPredicate;
    }
}




















