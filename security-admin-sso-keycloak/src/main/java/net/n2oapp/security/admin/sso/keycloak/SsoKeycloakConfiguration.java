package net.n2oapp.security.admin.sso.keycloak;

import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.impl.provider.SimpleSsoUserRoleProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * Конфигурация модуля взаимодействия с keycloak
 */
@Configuration
@DependsOn("liquibase")
@EnableConfigurationProperties(AdminSsoKeycloakProperties.class)
public class SsoKeycloakConfiguration {

    public static final String USER_SYNCHRONIZE_JOB_DETAIL = "User_Synchronize_Job_Detail";
    private static final String USER_SYNCHRONIZE_TRIGGER = "User_Synchronize_Trigger";

    @Bean
    @ConditionalOnExpression("${access.keycloak.sync-persistence-enabled:true}")
    SsoUserRoleProvider ssoUserRoleProvider(AdminSsoKeycloakProperties properties) {
        return new KeycloakSsoUserRoleProvider(properties);
    }

    @Bean
    @ConditionalOnExpression("${access.keycloak.sync-persistence-enabled:false}")
    SsoUserRoleProvider ssoUserRoleProvider() {
        return new SimpleSsoUserRoleProvider();
    }

    @Bean
    KeycloakRestRoleService keycloakRestRoleService(AdminSsoKeycloakProperties properties,
                                                    @Qualifier("keycloakWebClient") WebClient webClient) {
        return new KeycloakRestRoleService(properties, webClient);
    }

    @Bean
    KeycloakRestUserService keycloakRestUserService(AdminSsoKeycloakProperties properties,
                                                    @Qualifier("keycloakWebClient") WebClient webClient,
                                                    KeycloakRestRoleService roleService) {
        return new KeycloakRestUserService(properties, webClient, roleService);
    }

    @Bean
    @Qualifier("keycloakWebClient")
    public WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager, AdminSsoKeycloakProperties properties) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId(properties.getAdminClientId());
        HttpClient client = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(20));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(client))
                .apply(oauth2Client.oauth2Configuration())
                .build();
    }

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
                                                          OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
                                                          OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> tokenResponseClient) {
        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials(r -> r.accessTokenResponseClient(tokenResponseClient)).build();
        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, oAuth2AuthorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }

    @Bean
    OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> tokenResponseClient() {
        return new DefaultClientCredentialsTokenResponseClient();
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(AdminSsoKeycloakProperties properties) {
        ClientRegistration clientRegistration = ClientRegistration.
                withRegistrationId(properties.getAdminClientId())
                .clientId(properties.getAdminClientId())
                .clientSecret(properties.getAdminClientSecret())
                .tokenUri(String.format("%s/realms/%s/protocol/openid-connect/token", properties.getServerUrl(), properties.getRealm()))
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();
        return new InMemoryClientRegistrationRepository(clientRegistration);
    }
}
