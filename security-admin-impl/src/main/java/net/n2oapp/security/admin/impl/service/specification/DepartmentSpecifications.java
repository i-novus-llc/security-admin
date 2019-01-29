package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.DepartmentCriteria;
import net.n2oapp.security.admin.impl.entity.BankEntity_;
import net.n2oapp.security.admin.impl.entity.DepartmentEntity;
import net.n2oapp.security.admin.impl.entity.DepartmentEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


/**
 * Реализация фильтров для подразделения
 */
public class DepartmentSpecifications implements Specification<DepartmentEntity> {

    private DepartmentCriteria criteria;

    public DepartmentSpecifications(DepartmentCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<DepartmentEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        Predicate predicate = builder.and();

        if (criteria.getName() != null) {
            criteria.setName(criteria.getName().toLowerCase().replace(" ", ""));
            predicate = builder.and(predicate,
                    builder.or(
                            builder.like(builder.lower(builder.trim(root.get(DepartmentEntity_.fullName))), criteria.getName() + "%"),
                            builder.like(builder.lower(builder.trim(root.get(DepartmentEntity_.shortName))), criteria.getName() + "%")
                    ));
        }
        return predicate;
    }
}



