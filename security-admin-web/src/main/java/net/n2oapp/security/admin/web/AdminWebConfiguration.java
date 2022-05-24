package net.n2oapp.security.admin.web;

import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.framework.api.data.QueryExceptionHandler;
import net.n2oapp.framework.api.data.QueryProcessor;
import net.n2oapp.framework.config.register.InfoConstructor;
import net.n2oapp.framework.config.register.scanner.DefaultInfoScanner;
import net.n2oapp.framework.config.register.scanner.DefaultXmlInfoScanner;
import net.n2oapp.framework.engine.data.N2oInvocationFactory;
import net.n2oapp.framework.engine.data.N2oQueryProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminWebConfiguration {

    public static final String DEFAULT_PATTERN = "net/n2oapp/security/admin/web/default/**/*.xml";

    @Bean
    public DefaultInfoScanner<InfoConstructor> defaultInfoScanner() {
        return new DefaultXmlInfoScanner(DEFAULT_PATTERN);
    }

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
