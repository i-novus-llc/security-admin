package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.ServiceCriteria;
import net.n2oapp.security.admin.impl.entity.ServiceEntity;
import net.n2oapp.security.admin.impl.entity.ServiceEntity_;
import net.n2oapp.security.admin.impl.entity.SystemEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Реализация фильтров для служб
 */
public class ServiceSpecifications implements Specification<ServiceEntity> {
    private ServiceCriteria criteria;

    public ServiceSpecifications(ServiceCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<ServiceEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        Predicate predicate = builder.and();
        if (criteria.getSystemCode() != null)
            predicate = builder.and(predicate, builder.equal(root.get(ServiceEntity_.systemCode).get(SystemEntity_.CODE), criteria.getSystemCode()));
        return predicate;
    }
}




















