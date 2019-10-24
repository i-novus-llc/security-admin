package net.n2oapp.auth.gateway;

import net.n2oapp.auth.gateway.esia.EsiaAccessTokenProvider;
import net.n2oapp.auth.gateway.esia.EsiaUserInfoTokenServices;
import net.n2oapp.auth.gateway.esia.Pkcs7Util;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.auth.server.GatewayAccessTokenConverter;
import net.n2oapp.security.admin.auth.server.UserTokenConverter;
import net.n2oapp.security.admin.auth.server.logout.BackChannelLogoutHandler;
import net.n2oapp.security.admin.impl.service.EsiaUserDetailServiceImpl;
import net.n2oapp.security.admin.impl.service.UserDetailsServiceImpl;
import net.n2oapp.security.auth.common.AuthoritiesPrincipalExtractor;
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
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;
import ru.i_novus.ms.audit.client.UserAccessor;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableOAuth2Client
@EnableWebSecurity
@ComponentScan("net.n2oapp.security.admin.auth.server")
@Order(200)
public class AuthGatewayConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${access.auth.login-entry-point:/}")
    private String loginEntryPoint;

    @Value("${access.jwt.signing_key}")
    private String signingKey;

    @Value("${access.jwt.verifier_key}")
    private String verifierKey;

    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    @Autowired
    @Qualifier("UserDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Autowired
    @Qualifier("EsiaUserDetailServiceImpl")
    private UserDetailsService esiaUserDetailsService;

    @Autowired
    private BackChannelLogoutHandler logoutSuccessHandler;

    @Autowired
    private Pkcs7Util pkcs7Util;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/css/**", "/icons/**", "/fonts/**", "/public/**", "/static/**", "/webjars/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/", "/login**", "/api/**", "/css/**", "/icons/**", "/fonts/**", "/public/**", "/static/**", "/webjars/**").permitAll().anyRequest()
                .authenticated().and().exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(loginEntryPoint)).and().logout()
                .logoutSuccessUrl(loginEntryPoint).logoutSuccessHandler(logoutSuccessHandler).permitAll().and().csrf().disable()
                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
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

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(ssoKeycloakFilter(keycloak(), "/login/keycloak"));
        filters.add(ssoEsiaFilter(esia(), "/login/esia"));
        filter.setFilters(filters);
        return filter;
    }

    private Filter ssoKeycloakFilter(ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(
                path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        filter.setRestTemplate(template);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        AuthoritiesPrincipalExtractor extractor = new AuthoritiesPrincipalExtractor(userDetailsService).setAuthServer("KEYCLOAK");
        tokenServices.setAuthoritiesExtractor(extractor);
        tokenServices.setPrincipalExtractor(extractor);
        filter.setTokenServices(tokenServices);
        return filter;
    }

    private Filter ssoEsiaFilter(ClientResources client, String path) {
        Security.addProvider(new BouncyCastleProvider());
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        template.setAccessTokenProvider(new AccessTokenProviderChain(Arrays.asList(new EsiaAccessTokenProvider(pkcs7Util))));
        filter.setRestTemplate(template);
        EsiaUserInfoTokenServices tokenServices = new EsiaUserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        AuthoritiesPrincipalExtractor extractor = new AuthoritiesPrincipalExtractor(esiaUserDetailsService)
                .setAuthServer("ESIA").setPrincipalKeys("snils");
        tokenServices.setAuthoritiesExtractor(extractor);
        tokenServices.setPrincipalExtractor(extractor);
        filter.setTokenServices(tokenServices);
        return filter;
    }

    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter accessTokenConverter) {
        return new JwtTokenStore(accessTokenConverter);
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(signingKey);
        converter.setVerifierKey(verifierKey);
        converter.setAccessTokenConverter(new GatewayAccessTokenConverter(new UserTokenConverter()));
        return converter;
    }

    @Bean
    public UserAccessor userAccessor() {
        return () -> {
            String userId = "UNKNOWN";
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() != null) {
                if (auth.getPrincipal() instanceof net.n2oapp.security.auth.common.User) {
                    net.n2oapp.security.auth.common.User user = (net.n2oapp.security.auth.common.User) auth.getPrincipal();
                    userId = user.getEmail();
                } else {
                    userId = "" + auth.getPrincipal();
                }
            }
            return new ru.i_novus.ms.audit.client.model.User(userId, "UNKNOWN");
        };
    }

    /**
     * Клиенсткие ресурсы для протокола OAuth2
     */
    static class ClientResources {

        @NestedConfigurationProperty
        private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

        @NestedConfigurationProperty
        private ResourceServerProperties resource = new ResourceServerProperties();

        public AuthorizationCodeResourceDetails getClient() {
            return client;
        }

        public ResourceServerProperties getResource() {
            return resource;
        }
    }

}


