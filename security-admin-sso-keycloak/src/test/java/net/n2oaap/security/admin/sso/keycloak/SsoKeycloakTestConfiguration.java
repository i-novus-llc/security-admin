package net.n2oaap.security.admin.sso.keycloak;

import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.sso.keycloak.AdminSsoKeycloakProperties;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestRoleService;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestUserService;
import net.n2oapp.security.admin.sso.keycloak.KeycloakSsoUserRoleProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestOperations;

@TestConfiguration
public class SsoKeycloakTestConfiguration {

    @Bean
    SsoUserRoleProvider ssoUserRoleProvider(@Qualifier("adminSsoKeycloakProperties") AdminSsoKeycloakProperties properties) {
        return new KeycloakSsoUserRoleProvider(properties);
    }

    @Bean
    KeycloakRestRoleService keycloakRestRoleService(@Qualifier("adminSsoKeycloakProperties") AdminSsoKeycloakProperties properties,
                                                    @Qualifier("keycloakRestTemplate") RestOperations template) {
        return new KeycloakRestRoleService(properties, template);
    }

    @Bean
    KeycloakRestUserService keycloakRestUserService(@Qualifier("adminSsoKeycloakProperties") AdminSsoKeycloakProperties properties,
                                                    @Qualifier("keycloakRestTemplate") RestOperations template,
                                                    KeycloakRestRoleService roleService) {
        return new KeycloakRestUserService(properties, template, roleService);
    }

    @Bean
    OAuth2RestOperations keycloakRestTemplate(@Qualifier("adminSsoKeycloakProperties") AdminSsoKeycloakProperties properties) {
        ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
        resource.setClientId(properties.getAdminClientId());
        resource.setClientSecret(properties.getAdminClientSecret());
        resource.setAccessTokenUri(String.format("%s/realms/%s/protocol/openid-connect/token", properties.getServerUrl(), properties.getRealm()));
        return new OAuth2RestTemplate(resource);
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
