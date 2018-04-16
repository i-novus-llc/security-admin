package net.n2oapp.security.admin.impl;

import net.n2oapp.security.admin.impl.provider.SimpleSsoUserRoleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminImplConfiguration {

    @Bean
    public SimpleSsoUserRoleProvider simpleSsoUserRoleProvider() {
        return new SimpleSsoUserRoleProvider();
    }
}
