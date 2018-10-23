package net.n2oapp.security.admin.web;

import net.n2oapp.framework.api.context.ContextProcessor;
import net.n2oapp.framework.api.data.DomainProcessor;
import net.n2oapp.framework.api.data.QueryProcessor;
import net.n2oapp.framework.engine.data.N2oInvocationFactory;
import net.n2oapp.framework.engine.data.N2oQueryProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfiguration {
    @Bean
    public QueryProcessor queryProcessor(ContextProcessor contextProcessor,
                                         DomainProcessor domainProcessor,
                                         N2oInvocationFactory invocationFactory) {
        N2oQueryProcessor queryProcessor = new N2oQueryProcessor(contextProcessor, domainProcessor, invocationFactory);
        queryProcessor.setCriteriaResolver(new BaseCriteriaConstructor());
        return queryProcessor;
    }
}
