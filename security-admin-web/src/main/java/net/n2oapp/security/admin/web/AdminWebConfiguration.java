package net.n2oapp.security.admin.web;

import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.framework.api.data.QueryExceptionHandler;
import net.n2oapp.framework.api.data.QueryProcessor;
import net.n2oapp.framework.engine.data.N2oInvocationFactory;
import net.n2oapp.framework.engine.data.N2oQueryProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminWebConfiguration {
    @Bean
    public QueryProcessor saQueryProcessor(MetadataEnvironment environment,
                                           N2oInvocationFactory invocationFactory,
                                           QueryExceptionHandler exceptionHandler) {
        N2oQueryProcessor queryProcessor = new N2oQueryProcessor(invocationFactory, exceptionHandler);
        queryProcessor.setCriteriaResolver(new BaseCriteriaConstructor());
        queryProcessor.setPageStartsWith0(true);
        queryProcessor.setEnvironment(environment);
        return queryProcessor;
    }

    @Bean
    public OrganizationPagesBinder organizationPagesBinder() {
        return new OrganizationPagesBinder();
    }
}
