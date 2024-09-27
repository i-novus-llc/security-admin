package net.n2oapp.security.admin.impl.service.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import net.n2oapp.security.admin.api.criteria.RegionCriteria;
import net.n2oapp.security.admin.impl.entity.RegionEntity;
import net.n2oapp.security.admin.impl.entity.RegionEntity_;
import net.n2oapp.security.admin.impl.entity.base.RdmBaseEntity_;
import org.springframework.data.jpa.domain.Specification;

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
            predicate = builder.and(predicate, builder.like(builder.lower(root.get(RegionEntity_.name)), "%" + criteria.getName().toLowerCase() + "%"));

        predicate = builder.and(predicate, builder.isNull(root.get(RdmBaseEntity_.deletionDate)));
        return predicate;
    }
}




















