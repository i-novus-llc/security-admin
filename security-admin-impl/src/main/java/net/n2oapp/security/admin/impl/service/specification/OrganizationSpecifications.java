package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.impl.entity.OrganizationEntity;
import net.n2oapp.security.admin.impl.entity.OrganizationEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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

        return predicate;
    }
}