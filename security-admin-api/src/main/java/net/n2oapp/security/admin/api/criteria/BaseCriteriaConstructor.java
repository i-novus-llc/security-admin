package net.n2oapp.security.admin.api.criteria;

import net.n2oapp.framework.api.criteria.N2oPreparedCriteria;
import net.n2oapp.framework.api.data.CriteriaConstructor;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseCriteriaConstructor implements CriteriaConstructor, Serializable {
    @Override
    public <T> T construct(N2oPreparedCriteria criteria, Class<T> criteriaClass) {
        T instance;
        try {
            instance = criteriaClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
        if (instance instanceof BaseCriteria) {
            ((BaseCriteria)instance).setPage(criteria.getPage());
            ((BaseCriteria)instance).setSize(criteria.getSize());
            List<Sort.Order> orders = new ArrayList<>();
            if (criteria.getSorting() != null) {
                orders.add(new Sort.Order(Sort.Direction.fromString(criteria.getSorting()
                        .getDirection().getExpression()), criteria.getSorting().getField()));
            }
            ((BaseCriteria)instance).setOrders(orders);

            if (instance instanceof RoleCriteria) {
                ((RoleCriteria) instance).setPermissionIds(new ArrayList<>());
            } else if (instance instanceof UserCriteria) {
                ((UserCriteria) instance).setRoleIds(new ArrayList<>());
            }
        }
        return instance;
    }
}
