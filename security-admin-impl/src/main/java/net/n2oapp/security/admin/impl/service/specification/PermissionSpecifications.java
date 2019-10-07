package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.PermissionCriteria;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.PermissionEntity_;
import net.n2oapp.security.admin.impl.entity.SystemEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static java.util.Objects.nonNull;

public class PermissionSpecifications implements Specification<PermissionEntity> {
    private PermissionCriteria criteria;

    public PermissionSpecifications(PermissionCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<PermissionEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.and();
        if (nonNull(criteria.getSystemCode()))
            predicate = criteriaBuilder.equal(root.get(PermissionEntity_.systemCode).get(SystemEntity_.CODE),
                    criteria.getSystemCode());

        if (nonNull(criteria.getUserLevel()))
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(PermissionEntity_.userLevel),
                    UserLevel.valueOf(criteria.getUserLevel())));

        return predicate;
    }
}
