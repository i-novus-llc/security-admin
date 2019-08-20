package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.ApplicationCriteria;
import net.n2oapp.security.admin.impl.entity.ApplicationEntity;
import net.n2oapp.security.admin.impl.entity.ApplicationEntity_;
import net.n2oapp.security.admin.impl.entity.SystemEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Реализация фильтров для приложений
 */
public class ApplicationSpecifications implements Specification<ApplicationEntity> {
    private ApplicationCriteria criteria;

    public ApplicationSpecifications(ApplicationCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<ApplicationEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        Predicate predicate = builder.and();
        if (criteria.getSystemCode() != null)
            predicate = builder.and(predicate, builder.equal(root.get(ApplicationEntity_.systemCode).get(SystemEntity_.CODE), criteria.getSystemCode()));
        return predicate;
    }
}