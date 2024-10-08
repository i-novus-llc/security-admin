package net.n2oapp.security.admin.impl.service.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import net.n2oapp.security.admin.api.criteria.PermissionCriteria;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.PermissionEntity_;
import net.n2oapp.security.admin.impl.entity.SystemEntity_;
import org.springframework.data.jpa.domain.Specification;

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
        if (nonNull(criteria.getName()))
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get(PermissionEntity_.name)), "%" + criteria.getName().toLowerCase() + "%"));

        if (nonNull(criteria.getCode()) && Boolean.TRUE.equals(criteria.getWithoutParent()))
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.notLike(criteriaBuilder.lower(root.get(PermissionEntity_.code)), criteria.getCode().toLowerCase()));
        else if (nonNull(criteria.getCode()))
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get(PermissionEntity_.code)), "%" + criteria.getCode().toLowerCase() + "%"));


        if (nonNull(criteria.getSystemCode()))
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(PermissionEntity_.systemCode).get(SystemEntity_.CODE),
                    criteria.getSystemCode()));

        if (nonNull(criteria.getUserLevel()) && (isNull(criteria.getForForm()) || Boolean.FALSE.equals(criteria.getForForm()))) {
            String userLevel = criteria.getUserLevel().toUpperCase();
            // если userLevel не существует, то возвращаем false-предикат
            if (Arrays.stream(UserLevel.values()).map(UserLevel::getName).noneMatch(u -> u.equals(userLevel))) {
                return criteriaBuilder.disjunction();
            }
            if (UserLevel.NOT_SET.getName().equals(userLevel))
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.isNull(root.get(PermissionEntity_.userLevel)));
            else
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
