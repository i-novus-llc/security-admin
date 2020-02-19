package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.impl.entity.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;

/**
 * Реализация фильтров для организаций
 */
public class OrganizationSpecifications implements Specification<OrganizationEntity> {
    private OrganizationCriteria criteria;

    public OrganizationSpecifications(OrganizationCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<OrganizationEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        Predicate predicate = builder.and();
        if (criteria.getShortName() != null)
            predicate = builder.and(predicate, builder.like(builder.lower(root.get(OrganizationEntity_.shortName)), "%" + criteria.getShortName().toLowerCase() + "%"));

        if (criteria.getName() != null)
            predicate = builder.and(predicate, builder.or(builder.like(builder.lower(root.get(OrganizationEntity_.shortName)), "%" + criteria.getName().toLowerCase() + "%"),
                    builder.like(builder.lower(root.get(OrganizationEntity_.fullName)), "%" + criteria.getName().toLowerCase() + "%")));

        if (criteria.getOgrn() != null)
            predicate = builder.and(predicate, builder.like(builder.lower(root.get(OrganizationEntity_.ogrn)), "%" + criteria.getOgrn().toLowerCase() + "%"));

        if (criteria.getInn() != null)
            predicate = builder.and(predicate, builder.like(builder.lower(root.get(OrganizationEntity_.inn)), "%" + criteria.getInn().toLowerCase() + "%"));

        if (!CollectionUtils.isEmpty(criteria.getSystemCodes())) {
            Subquery subquery = criteriaQuery.subquery(String.class);
            Root subRoot = subquery.from(RoleEntity.class);
            ListJoin<RoleEntity, UserEntity> listJoin = subRoot.join(RoleEntity_.userList);
            subquery.select(subRoot.get(RoleEntity_.systemCode));
            subquery.where(builder.and(builder.equal(root.get(OrganizationEntity_.id), listJoin.get(UserEntity_.organization)),
                    subRoot.get(RoleEntity_.systemCode).get(SystemEntity_.CODE).in(criteria.getSystemCodes())));
            predicate = builder.and(predicate, builder.exists(subquery));
        }

        if (!CollectionUtils.isEmpty(criteria.getCategoryCodes())) {
            Subquery subquery = criteriaQuery.subquery(Integer.class);
            Root subRoot = subquery.from(OrgCategoryEntity.class);
            ListJoin<OrgCategoryEntity, OrganizationEntity> listJoin = subRoot.join(OrgCategoryEntity_.organizationList);
            subquery.select(subRoot.get(OrgCategoryEntity_.code));
            subquery.where(builder.and(builder.equal(root.get(OrganizationEntity_.id), listJoin.get(OrganizationEntity_.id)),
                    subRoot.get(OrgCategoryEntity_.code).in(criteria.getCategoryCodes())));
            predicate = builder.and(predicate, builder.exists(subquery));
        }
        return predicate;
    }
}