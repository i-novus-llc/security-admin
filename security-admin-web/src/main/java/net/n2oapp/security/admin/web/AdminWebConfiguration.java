package net.n2oapp.security.admin.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminWebConfiguration {

    @Bean
    public BaseCriteriaConstructor baseCriteriaConstructor() {
        return new BaseCriteriaConstructor();
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
