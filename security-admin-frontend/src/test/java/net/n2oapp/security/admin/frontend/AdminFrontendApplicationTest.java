package net.n2oapp.security.admin.frontend;

import net.n2oapp.criteria.api.Sorting;
import net.n2oapp.criteria.api.SortingDirectionEnum;
import net.n2oapp.framework.api.criteria.N2oPreparedCriteria;
import net.n2oapp.framework.api.data.CriteriaConstructorFactory;
import net.n2oapp.framework.api.data.QueryProcessor;
import net.n2oapp.framework.engine.data.N2oQueryProcessor;
import net.n2oapp.security.admin.api.criteria.BaseCriteria;
import net.n2oapp.security.admin.web.BaseCriteriaConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class AdminFrontendApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private QueryProcessor queryProcessor;

    @Autowired
    private CriteriaConstructorFactory criteriaConstructorFactory;

    @Autowired
    private BaseCriteriaConstructor baseCriteriaConstructor;

    @Test
    void contextLoads() {
        assertThat(context, notNullValue());
    }

    @Test
    void standardQueryProcessorIsUsed() {
        assertThat(context.getBeansOfType(QueryProcessor.class), aMapWithSize(1));
        assertThat(context.getBeansOfType(QueryProcessor.class), hasKey("queryProcessor"));
        assertThat(queryProcessor, instanceOf(N2oQueryProcessor.class));
        assertThat(context.containsBean("saQueryProcessor"), is(false));
    }

    @Test
    void baseCriteriaConstructorIsRegisteredAndUsedForSubclass() {
        assertThat(context.getBean(BaseCriteriaConstructor.class), sameInstance(baseCriteriaConstructor));

        N2oPreparedCriteria preparedCriteria = new N2oPreparedCriteria();
        preparedCriteria.setPage(3);
        preparedCriteria.setSize(25);
        preparedCriteria.addSorting(new Sorting("username", SortingDirectionEnum.DESC));

        TestCriteria criteria = new TestCriteria();
        TestCriteria result = criteriaConstructorFactory.construct(preparedCriteria, criteria);

        assertThat(result, sameInstance(criteria));
        assertThat(result.getPage(), is(2));
        assertThat(result.getSize(), is(25));
        assertThat(result.getOrders(), contains(new Sort.Order(Sort.Direction.DESC, "username")));
    }

    private static class TestCriteria extends BaseCriteria {
    }
}
