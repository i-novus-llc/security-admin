package net.n2oaap.security.admin.sso.keycloak;

import net.n2oapp.security.admin.sso.keycloak.AdminSsoKeycloakProperties;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestRoleService;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestUserService;
import net.n2oapp.security.admin.sso.keycloak.KeycloakSsoUserRoleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestConfig {
    @Autowired
    @Qualifier("adminSsoKeycloakProperties")
    private AdminSsoKeycloakProperties properties;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private KeycloakRestRoleService restRoleService;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public KeycloakSsoUserRoleProvider keycloakSsoUserRoleProvider() {
        return new KeycloakSsoUserRoleProvider(properties);
    }

    @Bean
    public KeycloakRestRoleService keycloakRestRoleService() {
        return new KeycloakRestRoleService(properties, restTemplate);
    }

    @Bean
    public KeycloakRestUserService keycloakRestUserService() {
        return new KeycloakRestUserService(properties, restTemplate, restRoleService);
    }
}
