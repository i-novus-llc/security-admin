package net.n2oaap.security.admin.sso.keycloak;

import net.n2oapp.security.admin.sso.keycloak.AdminSsoKeycloakProperties;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestRoleService;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestUserService;
import net.n2oapp.security.admin.sso.keycloak.KeycloakSsoUserRoleProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    RestClient restClient() {
        return RestClient.builder().build();
    }

    @Bean
    public KeycloakSsoUserRoleProvider keycloakSsoUserRoleProvider(AdminSsoKeycloakProperties properties) {
        return new KeycloakSsoUserRoleProvider(properties);
    }

    @Bean
    public KeycloakRestRoleService keycloakRestRoleService(AdminSsoKeycloakProperties properties, RestClient restClient) {
        return new KeycloakRestRoleService(properties, restClient);
    }

    @Bean
    public KeycloakRestUserService keycloakRestUserService(AdminSsoKeycloakProperties properties, RestClient restClient, KeycloakRestRoleService restRoleService) {
        return new KeycloakRestUserService(properties, restClient, restRoleService);
    }
}
