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

import java.util.Arrays;

import static java.util.Objects.isNull;
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

        if (nonNull(criteria.getUserLevel()) && (isNull(criteria.getForForm()) || Boolean.FALSE.equals(criteria.getForForm()))) {
            String userLevel = criteria.getUserLevel().toUpperCase();
            // если userLevel не существует, то возвращаем false-предикат
            if (Arrays.stream(UserLevel.values()).map(UserLevel::getName).noneMatch(u -> u.equals(userLevel))) {
                return criteriaBuilder.disjunction();
            }
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(PermissionEntity_.userLevel),
                    UserLevel.valueOf(userLevel)));
        }

        if (Boolean.TRUE.equals(criteria.getForForm()) && nonNull(criteria.getUserLevel())) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(criteriaBuilder.equal(root.get(PermissionEntity_.userLevel),
                    UserLevel.valueOf(criteria.getUserLevel())), criteriaBuilder.isNull(root.get(PermissionEntity_.USER_LEVEL))));
        } else if (Boolean.TRUE.equals(criteria.getForForm())) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.isNull(root.get(PermissionEntity_.userLevel)));
        }


        return predicate;
    }
}
