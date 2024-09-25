package net.n2oapp.security.admin.impl.service.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import net.n2oapp.security.admin.api.criteria.AccountCriteria;
import net.n2oapp.security.admin.impl.entity.AccountEntity;
import net.n2oapp.security.admin.impl.entity.AccountEntity_;
import net.n2oapp.security.admin.impl.entity.UserEntity_;
import org.springframework.data.jpa.domain.Specification;

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
        if (nonNull(criteria.getUsername()))
            predicate = builder.equal(root.get(AccountEntity_.user).get(UserEntity_.username), criteria.getUsername());


        return predicate;
    }
}
