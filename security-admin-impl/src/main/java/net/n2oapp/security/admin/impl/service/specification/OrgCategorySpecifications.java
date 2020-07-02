package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.OrgCategoryCriteria;
import net.n2oapp.security.admin.impl.entity.OrgCategoryEntity;
import net.n2oapp.security.admin.impl.entity.OrgCategoryEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Реализация фильтров для категорий организаций
 */
public class OrgCategorySpecifications implements Specification<OrgCategoryEntity> {

    private final OrgCategoryCriteria criteria;

    public OrgCategorySpecifications(OrgCategoryCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<OrgCategoryEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate predicate = builder.and();
        if (criteria.getName() != null)
            predicate = builder.and(predicate, builder.like(builder.lower(root.get(OrgCategoryEntity_.name)), "%" + criteria.getName().toLowerCase() + "%"));

        predicate = builder.and(predicate, builder.or(builder.equal(root.get(OrgCategoryEntity_.isDeleted), Boolean.FALSE), builder.isNull(root.get(OrgCategoryEntity_.isDeleted))));
        return predicate;
    }
}
