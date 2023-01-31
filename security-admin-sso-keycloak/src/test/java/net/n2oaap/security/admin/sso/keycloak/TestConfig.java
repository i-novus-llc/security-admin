package net.n2oaap.security.admin.sso.keycloak;

import net.n2oapp.security.admin.sso.keycloak.AdminSsoKeycloakProperties;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestRoleService;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestUserService;
import net.n2oapp.security.admin.sso.keycloak.KeycloakSsoUserRoleProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@TestConfiguration
public class TestConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    WebClient webClient(OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository, ClientRegistrationRepository clientRegistrationRepository) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository, oAuth2AuthorizedClientRepository);

        HttpClient client = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(20));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(client))
                .apply(oauth2Client.oauth2Configuration())
                .build();
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

    @Bean
    public KeycloakSsoUserRoleProvider keycloakSsoUserRoleProvider(AdminSsoKeycloakProperties properties) {
        return new KeycloakSsoUserRoleProvider(properties);
    }

    @Bean
    public KeycloakRestRoleService keycloakRestRoleService(AdminSsoKeycloakProperties properties, WebClient webClient) {
        return new KeycloakRestRoleService(properties, webClient);
    }

    @Bean
    public KeycloakRestUserService keycloakRestUserService(AdminSsoKeycloakProperties properties, WebClient webClient, KeycloakRestRoleService restRoleService) {
        return new KeycloakRestUserService(properties, webClient, restRoleService);
    }
}
