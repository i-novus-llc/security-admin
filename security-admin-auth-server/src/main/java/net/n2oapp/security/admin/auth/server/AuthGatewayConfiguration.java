package net.n2oapp.security.admin.auth.server;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.security.admin.auth.server.esia.EsiaAccessTokenProvider;
import net.n2oapp.security.admin.auth.server.esia.EsiaUserInfoTokenServices;
import net.n2oapp.security.admin.auth.server.esia.Pkcs7Util;
import net.n2oapp.security.admin.auth.server.exception.AuthenticationExceptionHandler;
import net.n2oapp.security.admin.auth.server.logout.OAuth2ProviderRedirectLogoutSuccessHandler;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.UserDetailsServiceImpl;
import net.n2oapp.security.auth.common.AuthoritiesPrincipalExtractor;
import net.n2oapp.security.auth.common.LogoutHandler;
import net.n2oapp.security.auth.common.UserAttributeKeys;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableOAuth2Client
@EnableWebSecurity
@ComponentScan
@Order(200)
public class AuthGatewayConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${access.auth.login-entry-point:/}")
    String loginEntryPoint;

    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    @Autowired
    UserDetailsServiceImpl gatewayUserDetailsService;

    @Autowired
    @Qualifier("esiaUserDetailsService")
    EsiaUserDetailsService esiaUserDetailsService;

    @Autowired
    List<LogoutHandler> logoutHandlers;

    @Autowired
    private Pkcs7Util pkcs7Util;

    @Autowired
    private UserAttributeKeys userAttributeKeys;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/css/**", "/icons/**", "/fonts/**", "/public/**", "/static/**", "/webjars/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/", "/login**", "/api/**", "/oauth/certs", "/css/**",
                "/icons/**", "/fonts/**", "/public/**", "/static/**", "/webjars/**").permitAll().anyRequest()
                .authenticated().and().exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(loginEntryPoint)).and().logout()
                .logoutSuccessUrl(loginEntryPoint)
                .logoutSuccessHandler(
                        new OAuth2ProviderRedirectLogoutSuccessHandler(logoutHandlers,
                                keycloak().getLogoutUri(), esia().getLogoutUri())).permitAll()
                .and().csrf().disable()
                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
    }

    @Bean
    @Primary
    public UserDetailsServiceImpl gatewayUserDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public EsiaUserDetailsService esiaUserDetailsService() {
        EsiaUserDetailsService esiaUserDetailsService = new EsiaUserDetailsService();
        esiaUserDetailsService.setSynchronizeFio(true);
        return esiaUserDetailsService;
    }

    @Bean
    public UserDetailsService refreshTokenUserDetailsService() {
        return new RefreshTokenUserDetailsService(userRepository, gatewayUserDetailsService);
    }

    @Bean
    public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(-100);
        filter.setRedirectStrategy(new DefaultRedirectStrategy() {
            @Override
            public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
                //в timestamp разница времени '+' записывается кодом, иначе timestamp не распарсится
                super.sendRedirect(request, response, url.replace("+", "%2B"));
            }
        });
        return registration;
    }

    @Bean
    @ConfigurationProperties("access.keycloak")
    public ClientResources keycloak() {
        return new ClientResources();
    }

    @Bean
    @ConfigurationProperties("access.esia")
    public ClientResources esia() {
        return new ClientResources();
    }

    protected Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(ssoKeycloakFilter(keycloak(), "/login/keycloak"));
        filters.add(ssoEsiaFilter(esia(), "/login/esia"));
        filter.setFilters(filters);
        return filter;
    }

    private Filter ssoKeycloakFilter(ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter filter = new GatewayOAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        filter.setRestTemplate(template);
        filter.setAuthenticationFailureHandler(new AuthenticationExceptionHandler());
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        AuthoritiesPrincipalExtractor extractor = new AuthoritiesPrincipalExtractor(gatewayUserDetailsService, "KEYCLOAK", userAttributeKeys);
        tokenServices.setAuthoritiesExtractor(extractor);
        tokenServices.setPrincipalExtractor(extractor);
        filter.setTokenServices(tokenServices);
        return filter;
    }

    private Filter ssoEsiaFilter(ClientResources client, String path) {
        Security.addProvider(new BouncyCastleProvider());
        OAuth2ClientAuthenticationProcessingFilter filter = new GatewayOAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        template.setAccessTokenProvider(new AccessTokenProviderChain(Arrays.asList(new EsiaAccessTokenProvider(pkcs7Util))));
        filter.setRestTemplate(template);
        filter.setAuthenticationFailureHandler(new AuthenticationExceptionHandler());
        EsiaUserInfoTokenServices tokenServices = new EsiaUserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        AuthoritiesPrincipalExtractor extractor = new AuthoritiesPrincipalExtractor(esiaUserDetailsService, "ESIA", userAttributeKeys)
                .setPrincipalKeys(Collections.singletonList("snils"));
        tokenServices.setAuthoritiesExtractor(extractor);
        tokenServices.setPrincipalExtractor(extractor);
        filter.setTokenServices(tokenServices);
        return filter;
    }

    /**
     * Клиенсткие ресурсы для протокола OAuth2
     */
    @Getter
    static class ClientResources {

        @NestedConfigurationProperty
        private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

        @NestedConfigurationProperty
        private ResourceServerProperties resource = new ResourceServerProperties();

        @Setter
        private String logoutUri;
    }
}


