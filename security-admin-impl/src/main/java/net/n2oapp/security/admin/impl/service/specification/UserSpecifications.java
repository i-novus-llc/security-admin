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
        if (nonNull(criteria.getUsername()))
            predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.username), criteria.getUsername()));
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
        if (!CollectionUtils.isEmpty(criteria.getRoleIds())) {
            Subquery sub = criteriaQuery.subquery(Integer.class);
            Root subRoot = sub.from(RoleEntity.class);
            ListJoin<RoleEntity, UserEntity> subUsers = subRoot.join(RoleEntity_.userList);
            sub.select(subRoot.get(RoleEntity_.id));
            sub.where(builder.and(builder.equal(root.get(UserEntity_.id), subUsers.get(UserEntity_.id)),
                    subRoot.get(RoleEntity_.id).in(criteria.getRoleIds())));
            predicate = builder.and(predicate, builder.exists(sub));
        }

        if (!CollectionUtils.isEmpty(criteria.getSystems())) {
            Subquery subquery = criteriaQuery.subquery(String.class);
            Root subRoot = subquery.from(RoleEntity.class);
            ListJoin<RoleEntity, UserEntity> listJoin = subRoot.join(RoleEntity_.userList);
            subquery.select(subRoot.get(RoleEntity_.systemCode));
            subquery.where(builder.and(builder.equal(root.get(UserEntity_.id), listJoin.get(UserEntity_.id)),
                    subRoot.get(RoleEntity_.systemCode).get(SystemEntity_.CODE).in(criteria.getSystems())));
            predicate = builder.and(predicate, builder.exists(subquery));
            builder.and(predicate, root.get(UserEntity_.ROLE_LIST));
        }
        if (criteria.getExtSys() != null) {
            predicate = builder.and(predicate, builder.equal(builder.upper(root.get(UserEntity_.extSys)), criteria.getExtSys().toUpperCase()));
        }
        if (nonNull(criteria.getUserLevel())) {
            String userLevel = criteria.getUserLevel().toUpperCase();
            // если userLevel не существует, то возвращаем false-предикат
            if (Arrays.stream(UserLevel.values()).map(UserLevel::getName).noneMatch(u -> u.equals(userLevel))) {
                return builder.disjunction();
            }
            if (UserLevel.NOT_SET.getName().equals(userLevel)) {
                predicate = builder.and(predicate, builder.isNull(root.get(UserEntity_.userLevel)));
            } else {
                predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.userLevel), UserLevel.valueOf(userLevel)));
            }
        }
        if (nonNull(criteria.getRegionId())) {
            predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.region).get(RegionEntity_.id), criteria.getRegionId()));
        }
        if (nonNull(criteria.getDepartmentId())) {
            predicate = builder.and(predicate, builder.equal(root.get(UserEntity_.department).get(DepartmentEntity_.id), criteria.getDepartmentId()));
        }
        if (!CollectionUtils.isEmpty(criteria.getOrganizations())) {
            CriteriaBuilder.In<Integer> in = builder.in(root.get(UserEntity_.organization).get(OrganizationEntity_.id));
            criteria.getOrganizations().forEach(in::value);
            predicate = builder.and(predicate, in);
        }
        return predicate;
    }
}



