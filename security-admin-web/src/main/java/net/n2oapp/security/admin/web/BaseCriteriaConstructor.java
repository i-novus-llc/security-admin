package net.n2oapp.security.admin.web;

import net.n2oapp.framework.api.criteria.N2oPreparedCriteria;
import net.n2oapp.framework.api.data.CriteriaConstructor;
import net.n2oapp.security.admin.api.criteria.BaseCriteria;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class BaseCriteriaConstructor implements CriteriaConstructor {
    @Override
    public Object construct(N2oPreparedCriteria criteria, Object instance) {
        if (instance instanceof BaseCriteria) {
            ((BaseCriteria) instance).setPage(criteria.getPage() - 1);
            ((BaseCriteria) instance).setSize(criteria.getSize());
            List<Sort.Order> orders = new ArrayList<>();
            if (criteria.getSorting() != null) {
                orders.add(new Sort.Order(Sort.Direction.fromString(criteria.getSorting()
                        .getDirection().getExpression()), criteria.getSorting().getField()));
            }
            ((BaseCriteria) instance).setOrders(orders);
        }
        return instance;
    }
}
