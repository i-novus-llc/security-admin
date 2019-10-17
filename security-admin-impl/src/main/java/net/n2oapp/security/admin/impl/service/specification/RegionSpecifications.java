package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.RegionCriteria;
import net.n2oapp.security.admin.impl.entity.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * Реализация фильтров для регионов
 */
public class RegionSpecifications implements Specification<RegionEntity> {
    private RegionCriteria criteria;

    public RegionSpecifications(RegionCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<RegionEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        Predicate predicate = builder.and();
        if (criteria.getName() != null)
            predicate = builder.and(predicate, builder.like(root.get(RegionEntity_.name), criteria.getName() + "%"));
        return predicate;
    }
}




















