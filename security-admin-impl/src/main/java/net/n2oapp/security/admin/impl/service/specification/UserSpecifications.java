package net.n2oapp.security.admin.impl.service.specification;

import jakarta.persistence.criteria.*;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.impl.entity.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;

import static java.util.Objects.nonNull;

/**
 * Реализация фильтров для юзера
 */
public class UserSpecifications implements Specification<UserEntity> {

    private transient UserCriteria criteria;

    public UserSpecifications(UserCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        Predicate predicate = userPredicate(root, builder);
        // account subquery
        Subquery<AccountEntity> accountSubquery = criteriaQuery.subquery(AccountEntity.class);
        Root<AccountEntity> accountSubqueryRoot = accountSubquery.from(AccountEntity.class);
        // role subquery
        Subquery<RoleEntity> roleSubquery = criteriaQuery.subquery(RoleEntity.class);
        Root<RoleEntity> roleSubqueryRoot = roleSubquery.from(RoleEntity.class);
        Predicate roleSubQueryPredicate = rolePredicate(roleSubqueryRoot, builder);

//        if (criteria.getExtSys() != null) {
//            predicate = builder.and(predicate, builder.equal(builder.upper(root.get(UserEntity_.extSys)), criteria.getExtSys().toUpperCase()));
//        }

        // если userLevel не существует, то возвращаем false-предикат
        if (nonNull(criteria.getUserLevel()) && !isExistingUserLevel(criteria.getUserLevel().toUpperCase()))
            return builder.disjunction();
        Predicate accountSubQueryPredicate = accountPredicate(accountSubqueryRoot, builder);

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

    /**
     * Условия на поля самого пользователя
     */
    private Predicate userPredicate(Root<UserEntity> root, CriteriaBuilder builder) {
        Predicate predicate = builder.and();
        if (nonNull(criteria.getUsername()))
            predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.username), criteria.getUsername()));

        if (nonNull(criteria.getEmail()))
            predicate = builder.and(predicate, builder.like(root.get(UserEntity_.email), "%" + criteria.getEmail() + "%"));

        if (nonNull(criteria.getFio()))
            predicate = builder.and(predicate, fioPredicate(root, builder));

        if (nonNull(criteria.getIsActive()))
            predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.isActive), criteria.getIsActive().equals("yes")));

        if (nonNull(criteria.getLastActionDate()))
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get(UserEntity_.lastActionDate), criteria.getLastActionDate()));
        return predicate;
    }

    private Predicate fioPredicate(Root<UserEntity> root, CriteriaBuilder builder) {
        criteria.setFio(criteria.getFio().toLowerCase().replace(" ", ""));
        return builder.or(
                builder.like(builder.lower(builder.trim(root.get(UserEntity_.name))), criteria.getFio() + "%"),
                builder.like(builder.lower(builder.trim(root.get(UserEntity_.patronymic))), criteria.getFio() + "%"),
                builder.like(builder.concat(builder.coalesce(builder.lower(builder.trim(root.get(UserEntity_.name))), ""),
                        builder.coalesce(builder.lower(builder.trim(root.get(UserEntity_.patronymic))), "")), criteria.getFio() + "%"),
                builder.like(builder.concat(builder.coalesce(builder.lower(builder.trim(root.get(UserEntity_.surname))), ""),
                        builder.concat(builder.coalesce(builder.lower(builder.trim(root.get(UserEntity_.name))), ""),
                                builder.coalesce(builder.lower(builder.trim(root.get(UserEntity_.patronymic))), ""))), criteria.getFio() + "%"));
    }

    /**
     * Условия подзапроса по ролям аккаунта
     */
    private Predicate rolePredicate(Root<RoleEntity> roleSubqueryRoot, CriteriaBuilder builder) {
        Predicate roleSubQueryPredicate = builder.and();
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
                    roleSubqueryRoot.get(RoleEntity_.system).get(SystemEntity_.code).in(criteria.getSystems()));
        }
        return roleSubQueryPredicate;
    }

    /**
     * Условия подзапроса по аккаунтам пользователя
     */
    private Predicate accountPredicate(Root<AccountEntity> accountSubqueryRoot, CriteriaBuilder builder) {
        Predicate accountSubQueryPredicate = builder.and();
        if (nonNull(criteria.getUserLevel()))
            accountSubQueryPredicate = builder.and(accountSubQueryPredicate,
                    userLevelPredicate(accountSubqueryRoot, builder, criteria.getUserLevel().toUpperCase()));

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

        if (!CollectionUtils.isEmpty(criteria.getOrganizationIds())) {
            accountSubQueryPredicate = builder.and(accountSubQueryPredicate,
                    accountSubqueryRoot.get(AccountEntity_.organization).get(OrganizationEntity_.id).in(criteria.getOrganizationIds()));
        }
        return accountSubQueryPredicate;
    }

    private Predicate userLevelPredicate(Root<AccountEntity> accountSubqueryRoot, CriteriaBuilder builder, String userLevel) {
        if (UserLevel.NOT_SET.getName().equals(userLevel))
            return builder.isNull(accountSubqueryRoot.get(AccountEntity_.userLevel));
        return builder.equal(accountSubqueryRoot.get(AccountEntity_.userLevel), UserLevel.valueOf(userLevel));
    }

    private boolean isExistingUserLevel(String userLevel) {
        return Arrays.stream(UserLevel.values()).map(UserLevel::getName).anyMatch(u -> u.equals(userLevel));
    }
}



