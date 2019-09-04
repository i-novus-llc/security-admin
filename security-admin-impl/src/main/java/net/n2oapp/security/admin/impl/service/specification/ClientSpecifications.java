package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.ClientCriteria;
import net.n2oapp.security.admin.impl.entity.ClientEntity;
import net.n2oapp.security.admin.impl.entity.ClientEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


/**
 * Реализация фильтров для клиентов
 */
public class ClientSpecifications implements Specification<ClientEntity> {
    private ClientCriteria criteria;

    public ClientSpecifications(ClientCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<ClientEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate predicate = builder.and();
        if (criteria.getClientId() != null)
            predicate = builder.and(predicate, builder.equal(root.get(ClientEntity_.clientId), criteria.getClientId()));
        return predicate;
    }
}
