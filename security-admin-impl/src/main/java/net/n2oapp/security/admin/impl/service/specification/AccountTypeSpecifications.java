package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.AccountTypeCriteria;
import net.n2oapp.security.admin.impl.entity.AccountTypeEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class AccountTypeSpecifications implements Specification<AccountTypeEntity> {

    private final AccountTypeCriteria criteria;

    public AccountTypeSpecifications(AccountTypeCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<AccountTypeEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return builder.and();
    }
}
