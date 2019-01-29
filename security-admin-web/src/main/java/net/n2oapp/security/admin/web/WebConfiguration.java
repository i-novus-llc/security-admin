package net.n2oapp.security.admin.web;

import net.n2oapp.framework.access.data.SecurityProvider;
import net.n2oapp.framework.access.simple.PermissionApi;
import net.n2oapp.framework.api.context.ContextProcessor;
import net.n2oapp.framework.api.data.DomainProcessor;
import net.n2oapp.framework.api.data.QueryProcessor;
import net.n2oapp.framework.engine.data.N2oInvocationFactory;
import net.n2oapp.framework.engine.data.N2oQueryProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ExceptionHandlerConfiguration.class)
public class WebConfiguration {
    @Bean
    public QueryProcessor queryProcessor(ContextProcessor contextProcessor,
                                         DomainProcessor domainProcessor,
                                         N2oInvocationFactory invocationFactory) {
        N2oQueryProcessor queryProcessor = new N2oQueryProcessor(invocationFactory, contextProcessor, domainProcessor);
        queryProcessor.setCriteriaResolver(new BaseCriteriaConstructor());
        return queryProcessor;
    }

    @Bean //todo убрать в 7.0.4
    public SecurityProvider securityProvider(PermissionApi permissionApi) {
        return new SecurityProvider(permissionApi);
    }
}
