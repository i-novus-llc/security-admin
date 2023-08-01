package net.n2oapp.security.auth;

import net.n2oapp.framework.access.simple.PermissionApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PermissionApiConfiguration {
    @Bean
    public PermissionApi securitySimplePermissionApi() {
        return new SecuritySimplePermissionApi();
    }
}