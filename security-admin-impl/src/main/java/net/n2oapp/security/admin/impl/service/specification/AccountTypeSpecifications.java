package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.AccountTypeCriteria;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.impl.entity.AccountTypeEntity;
import net.n2oapp.security.admin.impl.entity.AccountTypeEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.Arrays;

import static org.springframework.util.StringUtils.hasText;

public class AccountTypeSpecifications implements Specification<AccountTypeEntity> {

    private final AccountTypeCriteria criteria;

    public AccountTypeSpecifications(AccountTypeCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<AccountTypeEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate predicate = builder.and();
        if (hasText(criteria.getName()))
            predicate = builder.and(predicate, builder.like(builder.lower(root.get(AccountTypeEntity_.NAME)), "%" + criteria.getName().toLowerCase() + "%"));
        if (hasText(criteria.getUserLevel())) {
            String userLevel = criteria.getUserLevel().toUpperCase();
            if (Arrays.stream(UserLevel.values()).map(UserLevel::getName).noneMatch(u -> u.equals(userLevel))) {
                return builder.disjunction();
            }
            if (UserLevel.NOT_SET.getName().equals(userLevel)) {
                predicate = builder.and(predicate, builder.isNull(root.get(AccountTypeEntity_.userLevel)));
            } else {
                predicate = builder.and(predicate, builder.equal(root.get(AccountTypeEntity_.userLevel), UserLevel.valueOf(userLevel)));
            }
        }
        return predicate;
    }
}
