package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.impl.entity.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.Collection;

import static java.util.Objects.nonNull;

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
        // account subquery
        Subquery<AccountEntity> accountSubquery = criteriaQuery.subquery(AccountEntity.class);
        Root<AccountEntity> accountSubqueryRoot = accountSubquery.from(AccountEntity.class);
        Predicate accountSubQueryPredicate = builder.and();
        // role subquery
        Subquery<RoleEntity> roleSubquery = criteriaQuery.subquery(RoleEntity.class);
        Root<RoleEntity> roleSubqueryRoot = roleSubquery.from(RoleEntity.class);
        Predicate roleSubQueryPredicate = builder.and();

        if (nonNull(criteria.getUsername()))
            predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.username), criteria.getUsername()));

        if (nonNull(criteria.getEmail()))
            predicate = builder.and(predicate, builder.like(root.get(UserEntity_.email), "%" + criteria.getEmail() + "%"));

        if (nonNull(criteria.getFio())) {
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

        if (nonNull(criteria.getIsActive())) {
            if (criteria.getIsActive().equals("yes")) {
                predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.isActive), true));
            } else {
                predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.isActive), false));
            }
        }

        if (!CollectionUtils.isEmpty(criteria.getRoleCodes())) {
            roleSubQueryPredicate = builder.and(roleSubQueryPredicate,
                    roleSubqueryRoot.get(RoleEntity_.code).in(criteria.getRoleCodes()));
        }

        if (!CollectionUtils.isEmpty(criteria.getRoleIds())) {
            roleSubQueryPredicate = builder.and(roleSubQueryPredicate,
                    roleSubqueryRoot.get(RoleEntity_.id).in(criteria.getRoleIds()));
        }

        if (!CollectionUtils.isEmpty(criteria.getSystems())) {
            roleSubQueryPredicate = builder.and(roleSubQueryPredicate,
                    roleSubqueryRoot.get(RoleEntity_.systemCode).get(SystemEntity_.code).in(criteria.getSystems()));
        }

//        if (criteria.getExtSys() != null) {
//            predicate = builder.and(predicate, builder.equal(builder.upper(root.get(UserEntity_.extSys)), criteria.getExtSys().toUpperCase()));
//        }

        if (nonNull(criteria.getUserLevel())) {
            String userLevel = criteria.getUserLevel().toUpperCase();
            // если userLevel не существует, то возвращаем false-предикат
            if (Arrays.stream(UserLevel.values()).map(UserLevel::getName).noneMatch(u -> u.equals(userLevel)))
                return builder.disjunction();

            if (UserLevel.NOT_SET.getName().equals(userLevel))
                accountSubQueryPredicate = builder.and(accountSubQueryPredicate,
                        builder.isNull(accountSubqueryRoot.get(AccountEntity_.userLevel)));
            else
                accountSubQueryPredicate = builder.and(accountSubQueryPredicate,
                        builder.equal(accountSubqueryRoot.get(AccountEntity_.userLevel), UserLevel.valueOf(userLevel)));
        }

        if (nonNull(criteria.getRegionId())) {
            accountSubQueryPredicate = builder.and(accountSubQueryPredicate,
                    builder.equal(accountSubqueryRoot.get(AccountEntity_.region).get(RegionEntity_.id), criteria.getRegionId()));
        }

        if (nonNull(criteria.getDepartmentId())) {
            accountSubQueryPredicate = builder.and(accountSubQueryPredicate,
                    builder.equal(accountSubqueryRoot.get(AccountEntity_.department).get(DepartmentEntity_.id), criteria.getDepartmentId()));
        }

        if (nonNull(criteria.getOrganizationId())) {
            accountSubQueryPredicate = builder.and(accountSubQueryPredicate,
                    builder.equal(accountSubqueryRoot.get(AccountEntity_.organization).get(OrganizationEntity_.id), criteria.getOrganizationId()));
        }

        if (nonNull(criteria.getLastActionDate())) {
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get(UserEntity_.lastActionDate), criteria.getLastActionDate()));
        }

        // add subquery only if criteria has any subquery condition
        if (roleSubQueryPredicate.getExpressions().size() > 1) {
            Expression<Collection<RoleEntity>> accountRoles = accountSubqueryRoot.get(AccountEntity_.ROLE_LIST);
            roleSubQueryPredicate = builder.and(roleSubQueryPredicate,
                    builder.isMember(roleSubqueryRoot, accountRoles));
            roleSubquery.select(roleSubqueryRoot).where(roleSubQueryPredicate);
            accountSubQueryPredicate = builder.and(accountSubQueryPredicate, builder.exists(roleSubquery));
        }
        if (accountSubQueryPredicate.getExpressions().size() > 1) {
            accountSubQueryPredicate = builder.and(accountSubQueryPredicate, builder.equal(root.get(UserEntity_.id),
                    accountSubqueryRoot.get(AccountEntity_.user).get(UserEntity_.id)));
            accountSubquery.select(accountSubqueryRoot).where(accountSubQueryPredicate);
            predicate = builder.and(predicate, builder.exists(accountSubquery));
        }
        return predicate;
    }
}



