package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Реализация фильтров для систем
 */
public class SystemSpecifications implements Specification<SystemEntity> {
    private SystemCriteria criteria;

    public SystemSpecifications(SystemCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<SystemEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        Predicate predicate = builder.and();
        if (criteria.getCode() != null)
            predicate = builder.and(predicate, builder.like(root.get(SystemEntity_.code), criteria.getCode() + "%"));
        if (criteria.getName() != null)
            predicate = builder.and(predicate, builder.like(root.get(SystemEntity_.name), criteria.getName() + "%"));
        return predicate;
    }
}




















