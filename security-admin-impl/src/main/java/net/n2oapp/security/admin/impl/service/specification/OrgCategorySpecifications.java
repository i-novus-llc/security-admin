package net.n2oapp.security.admin.impl.service.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import net.n2oapp.security.admin.api.criteria.OrgCategoryCriteria;
import net.n2oapp.security.admin.impl.entity.OrgCategoryEntity;
import net.n2oapp.security.admin.impl.entity.OrgCategoryEntity_;
import net.n2oapp.security.admin.impl.entity.base.RdmBaseEntity_;
import org.springframework.data.jpa.domain.Specification;

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

        predicate = builder.and(predicate, builder.isNull(root.get(RdmBaseEntity_.deletionDate)));
        return predicate;
    }
}
