package net.n2oaap.security.admin.sso.keycloak;

import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.sso.keycloak.AdminSsoKeycloakProperties;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestRoleService;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestUserService;
import net.n2oapp.security.admin.sso.keycloak.KeycloakSsoUserRoleProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class SsoKeycloakTestConfiguration {

    @Bean
    SsoUserRoleProvider ssoUserRoleProvider(@Qualifier("adminSsoKeycloakProperties") AdminSsoKeycloakProperties properties) {
        return new KeycloakSsoUserRoleProvider(properties);
    }

    @Bean
    KeycloakRestRoleService keycloakRestRoleService(@Qualifier("adminSsoKeycloakProperties") AdminSsoKeycloakProperties properties,
                                                    @Qualifier("keycloakWebClient") WebClient webClient) {
        return new KeycloakRestRoleService(properties, webClient);
    }

    @Bean
    KeycloakRestUserService keycloakRestUserService(@Qualifier("adminSsoKeycloakProperties") AdminSsoKeycloakProperties properties,
                                                    @Qualifier("keycloakWebClient") WebClient webClient,
                                                    KeycloakRestRoleService roleService) {
        return new KeycloakRestUserService(properties, webClient, roleService);
    }

    @Bean
    @Qualifier("keycloakWebClient")
    WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        return WebClient.builder()
                .apply(oauth2Client.oauth2Configuration())
                .build();
    }

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(OAuth2AuthorizedClientRepository authorizedClientRepository, @Qualifier("adminSsoKeycloakProperties") AdminSsoKeycloakProperties properties) {
        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();
        ClientRegistration clientRegistration = ClientRegistration.
                withRegistrationId(properties.getAdminClientId())
                .clientId(properties.getAdminClientId())
                .clientSecret(properties.getAdminClientSecret())
                .tokenUri(String.format("%s/realms/%s/protocol/openid-connect/token", properties.getServerUrl(), properties.getRealm()))
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();
        ClientRegistrationRepository clientRegistrationRepository = new InMemoryClientRegistrationRepository(clientRegistration);
        DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
