package net.n2oapp.security.admin.sso.keycloak;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Конфигурация модуля взаимодействия с keycloak
 */
@Configuration
@EnableConfigurationProperties(SsoKeycloakProperties.class)
public class SsoKeycloakConfiguration {

    @Bean
    @Primary
    KeycloakSsoUserRoleProvider keycloakSsoUserRoleProvider(SsoKeycloakProperties properties) {
        return new KeycloakSsoUserRoleProvider(properties);
    }


    @Bean
    @Primary
    KeycloakRestRoleService keycloakRestRoleService(SsoKeycloakProperties properties) {
        return new KeycloakRestRoleService(properties);
    }

    @Bean
    @Primary
    KeycloakRestUserService keycloakRestUserService(SsoKeycloakProperties properties) {
        return new KeycloakRestUserService(properties);
    }
}
