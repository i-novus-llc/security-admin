package net.n2oapp.security.admin.impl.service.specification;

import net.n2oapp.security.admin.api.criteria.BankCriteria;
import net.n2oapp.security.admin.impl.entity.BankEntity;
import net.n2oapp.security.admin.impl.entity.BankEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


/**
 * Реализация фильтров для банка
 */
public class BankSpecifications implements Specification<BankEntity> {

    private BankCriteria criteria;

    public BankSpecifications(BankCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<BankEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        Predicate predicate = builder.and();

        if (criteria.getName() != null) {
            criteria.setName(criteria.getName().toLowerCase().replace(" ", ""));
            predicate = builder.and(predicate,
                    builder.or(
                            builder.like(builder.lower(builder.trim(root.get(BankEntity_.fullName))), criteria.getName() + "%"),
                            builder.like(builder.lower(builder.trim(root.get(BankEntity_.shortName))), criteria.getName() + "%"),
                            builder.like(builder.lower(builder.trim(root.get(BankEntity_.regNum))), criteria.getName() + "%"),
                            builder.like(builder.lower(builder.trim(root.get(BankEntity_.inn))), criteria.getName() + "%"),
                            builder.like(builder.lower(builder.trim(root.get(BankEntity_.ogrn))), criteria.getName() + "%"),
                            builder.like(builder.lower(builder.trim(root.get(BankEntity_.bik))), criteria.getName() + "%"),
                            builder.like(builder.lower(builder.trim(root.get(BankEntity_.kpp))), criteria.getName() + "%"),
                            builder.like(builder.lower(builder.trim(root.get(BankEntity_.legalAddress))), criteria.getName() + "%"),
                            builder.like(builder.lower(builder.trim(root.get(BankEntity_.actualAddress))), criteria.getName() + "%")
                    ));
        }
        if (criteria.getType() != null) {
            switch (criteria.getType()) {
                case PARENT:
                    predicate = builder.and(predicate, builder.isNull(root.get(BankEntity_.parent)));
                    break;
                case DEPARTMENT:
                    predicate = builder.and(predicate, builder.isNotNull(root.get(BankEntity_.parent)));
                    break;
                default:
                    return null;
            }
        }

        if (criteria.getParent() != null) {
            predicate = builder.and(predicate, builder.equal(root.get(BankEntity_.parent), criteria.getParent()));
        }
        return predicate;
    }
}



