package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.impl.entity.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.util.Arrays;

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
        Subquery<AccountEntity> subquery = criteriaQuery.subquery(AccountEntity.class);
        Root<AccountEntity> subqueryRoot = subquery.from(AccountEntity.class);
        subquery.select(subqueryRoot);
        Predicate subQueryPredicate = builder.and();
        subQueryPredicate = builder.and(subQueryPredicate, builder.equal(root.get(UserEntity_.id),
                subqueryRoot.get(AccountEntity_.USER).get(UserEntity_.ID)));

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
            Subquery<String> sub = criteriaQuery.subquery(String.class);
            Root<RoleEntity> subRoot = sub.from(RoleEntity.class);
            ListJoin<RoleEntity, AccountEntity> subAccounts = subRoot.join(RoleEntity_.accountList);
            sub.select(subRoot.get(RoleEntity_.code));
            sub.where(builder.and(builder.equal(subqueryRoot.get(AccountEntity_.id), subAccounts.get(AccountEntity_.id)),
                    subRoot.get(RoleEntity_.code).in(criteria.getRoleCodes())));
            subQueryPredicate = builder.and(subQueryPredicate, builder.exists(sub));
        }

        if (!CollectionUtils.isEmpty(criteria.getRoleIds())) {
            Subquery<Integer> sub = criteriaQuery.subquery(Integer.class);
            Root<RoleEntity> subRoot = sub.from(RoleEntity.class);
            ListJoin<RoleEntity, AccountEntity> subAccounts = subRoot.join(RoleEntity_.accountList);
            sub.select(subRoot.get(RoleEntity_.id));
            sub.where(builder.and(builder.equal(subqueryRoot.get(AccountEntity_.id), subAccounts.get(AccountEntity_.id)),
                    subRoot.get(RoleEntity_.id).in(criteria.getRoleIds())));
            subQueryPredicate = builder.and(subQueryPredicate, builder.exists(sub));
        }

        if (!CollectionUtils.isEmpty(criteria.getSystems())) {
            Subquery<String> sub = criteriaQuery.subquery(String.class);
            Root subRoot = sub.from(RoleEntity.class);
            ListJoin<RoleEntity, AccountEntity> listJoin = subRoot.join(RoleEntity_.accountList);
            sub.select(subRoot.get(RoleEntity_.systemCode));
            sub.where(builder.and(builder.equal(subqueryRoot.get(AccountEntity_.id), listJoin.get(AccountEntity_.id)),
                    subRoot.get(RoleEntity_.systemCode).get(SystemEntity_.CODE).in(criteria.getSystems())));
            subQueryPredicate = builder.and(subQueryPredicate, builder.exists(sub));
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
                subQueryPredicate = builder.and(subQueryPredicate,
                        builder.isNull(subqueryRoot.get(AccountEntity_.USER_LEVEL)));
            else
                subQueryPredicate = builder.and(subQueryPredicate,
                        builder.equal(subqueryRoot.get(AccountEntity_.USER_LEVEL), UserLevel.valueOf(userLevel)));
        }

        if (nonNull(criteria.getRegionId())) {
            subQueryPredicate = builder.and(subQueryPredicate,
                    builder.equal(subqueryRoot.get(AccountEntity_.REGION).get(RegionEntity_.ID), criteria.getRegionId()));
        }

        if (nonNull(criteria.getDepartmentId())) {
            subQueryPredicate = builder.and(subQueryPredicate,
                    builder.equal(subqueryRoot.get(AccountEntity_.DEPARTMENT).get(DepartmentEntity_.ID), criteria.getDepartmentId()));
        }

        // TODO Для организаций в фильтре таблицы используется не мульти, а сингл режим, а здесь обрабатывается, как мульти
        //    В чем смысл, Карл?
//        if (!CollectionUtils.isEmpty(criteria.getOrganizations())) {
//            CriteriaBuilder.In<Integer> in = builder.in(root.get(UserEntity_.organization).get(OrganizationEntity_.id));
//            criteria.getOrganizations().forEach(in::value);
//            predicate = builder.and(predicate, in);
//        }
        if (nonNull(criteria.getLastActionDate())) {
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get(UserEntity_.lastActionDate), criteria.getLastActionDate()));
        }

        subquery.select(subqueryRoot).where(subQueryPredicate);
        predicate = builder.and(predicate, builder.exists(subquery));
        return predicate;
    }
}



