package net.n2oapp.security.admin.sso.keycloak;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация модуля взаимодействия с keycloak
 */
@Configuration
@EnableConfigurationProperties(SsoKeycloakProperties.class)
public class SsoKeycloakConfiguration {

    @Bean
    SsoUserRoleProviderImpl keycloakUtil(SsoKeycloakProperties properties) {
        return new SsoUserRoleProviderImpl(properties);
    }


}
