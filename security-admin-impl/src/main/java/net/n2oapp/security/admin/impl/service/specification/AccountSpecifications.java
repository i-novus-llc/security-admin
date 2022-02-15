package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.AccountCriteria;
import net.n2oapp.security.admin.impl.entity.AccountEntity;
import net.n2oapp.security.admin.impl.entity.AccountEntity_;
import net.n2oapp.security.admin.impl.entity.UserEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static java.util.Objects.nonNull;

public class AccountSpecifications implements Specification<AccountEntity> {

    private final AccountCriteria criteria;

    public AccountSpecifications(AccountCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<AccountEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate predicate = builder.and();
        if (nonNull(criteria.getUserId()))
            predicate = builder.equal(root.get(AccountEntity_.user).get(UserEntity_.id), criteria.getUserId());
        if (nonNull(criteria.getUserName()))
            predicate = builder.equal(root.get(AccountEntity_.user).get(UserEntity_.username), criteria.getUserName());


        return predicate;
    }
}
