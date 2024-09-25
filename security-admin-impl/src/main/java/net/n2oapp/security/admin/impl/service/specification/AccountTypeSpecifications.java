package net.n2oapp.security.admin.impl.service.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import net.n2oapp.security.admin.api.criteria.AccountTypeCriteria;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.impl.entity.AccountTypeEntity;
import net.n2oapp.security.admin.impl.entity.AccountTypeEntity_;
import org.springframework.data.jpa.domain.Specification;

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
                predicate = builder.and(predicate, builder.or(builder.isNull(root.get(AccountTypeEntity_.userLevel)),
                        builder.equal(root.get(AccountTypeEntity_.userLevel), UserLevel.NOT_SET)));
            } else {
                predicate = builder.and(predicate, builder.equal(root.get(AccountTypeEntity_.userLevel), UserLevel.valueOf(userLevel)));
            }
        }
        return predicate;
    }
}
