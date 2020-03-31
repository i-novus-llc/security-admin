package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.DepartmentCriteria;
import net.n2oapp.security.admin.impl.entity.DepartmentEntity;
import net.n2oapp.security.admin.impl.entity.DepartmentEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Реализация фильтров для организаций
 */
public class DepartmentSpecifications implements Specification<DepartmentEntity> {
    private DepartmentCriteria criteria;

    public DepartmentSpecifications(DepartmentCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<DepartmentEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        Predicate predicate = builder.and();
        if (criteria.getName() != null)
            predicate = builder.and(predicate, builder.like(builder.lower(root.get(DepartmentEntity_.name)), "%" + criteria.getName().toLowerCase() + "%"));

        predicate = builder.and(predicate, builder.equal(root.get(DepartmentEntity_.isDeleted), Boolean.FALSE));
        return predicate;
    }
}




















