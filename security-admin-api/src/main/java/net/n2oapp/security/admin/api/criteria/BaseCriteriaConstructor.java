package net.n2oapp.security.admin.api.criteria;

import net.n2oapp.framework.api.criteria.N2oPreparedCriteria;
import net.n2oapp.framework.api.data.CriteriaConstructor;

import java.io.Serializable;

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
        }
        return instance;
    }
}
