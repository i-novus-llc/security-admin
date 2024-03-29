package net.n2oapp.security.admin.web;

import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.framework.api.data.QueryExceptionHandler;
import net.n2oapp.framework.api.data.QueryProcessor;
import net.n2oapp.framework.engine.data.N2oInvocationFactory;
import net.n2oapp.framework.engine.data.N2oQueryProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminWebConfiguration {

    @Bean
    public QueryProcessor saQueryProcessor(MetadataEnvironment environment,
                                           N2oInvocationFactory invocationFactory,
                                           QueryExceptionHandler exceptionHandler) {
        N2oQueryProcessor queryProcessor = new N2oQueryProcessor(invocationFactory, exceptionHandler);
        queryProcessor.setCriteriaConstructor(new BaseCriteriaConstructor());
        queryProcessor.setPageStartsWith0(true);
        queryProcessor.setEnvironment(environment);
        return queryProcessor;
    }

    @Configuration
    @ConditionalOnProperty(value = "access.organization-persist-mode", havingValue = "sync")
    public class OrganizationPersistButtonDisplayConfiguration {

        @Bean
        public OrganizationPersistButtonTransformer organizationPersistButtonTransformer() {
            return new OrganizationPersistButtonTransformer();
        }

        @Bean
        public OrganizationPersistButtonSourceTransformer organizationPersistButtonSourceTransformer() {
            return new OrganizationPersistButtonSourceTransformer();
        }
    }
}
