package net.n2oapp.security.admin.impl;

import net.n2oapp.security.admin.impl.provider.SimpleSsoUserRoleProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminImplConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public SimpleSsoUserRoleProvider simpleSsoUserRoleProvider() {
        return new SimpleSsoUserRoleProvider();
    }
}
