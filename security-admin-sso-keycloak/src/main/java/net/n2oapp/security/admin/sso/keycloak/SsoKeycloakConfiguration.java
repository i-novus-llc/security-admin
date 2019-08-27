package net.n2oapp.security.admin.sso.keycloak;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

/**
 * Конфигурация модуля взаимодействия с keycloak
 */
@Configuration
@EnableConfigurationProperties(AdminSsoKeycloakProperties.class)
public class SsoKeycloakConfiguration {

    @Bean
    @Primary
    KeycloakSsoUserRoleProvider keycloakSsoUserRoleProvider(AdminSsoKeycloakProperties properties) {
        return new KeycloakSsoUserRoleProvider(properties);
    }


    @Bean
    KeycloakRestRoleService keycloakRestRoleService(AdminSsoKeycloakProperties properties) {
        return new KeycloakRestRoleService(properties);
    }

    @Bean
    KeycloakRestUserService keycloakRestUserService(AdminSsoKeycloakProperties properties) {
        return new KeycloakRestUserService(properties);
    }

    @Bean
    OAuth2RestOperations keycloakRestTemplate(AdminSsoKeycloakProperties properties) {
        ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
        resource.setClientId(properties.getAdminClientId());
        resource.setClientSecret(properties.getAdminClientSecret());
        resource.setAccessTokenUri(String.format("%s/realms/%s/protocol/openid-connect/token", properties.getServerUrl(), properties.getRealm()));
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resource);
        return restTemplate;
    }
}
