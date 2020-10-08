package net.n2oapp.security.admin.auth.server;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.security.admin.auth.server.logout.OAuth2ProviderRedirectLogoutSuccessHandler;
import net.n2oapp.security.admin.impl.service.UserDetailsServiceImpl;
import net.n2oapp.security.auth.common.AuthoritiesPrincipalExtractor;
import net.n2oapp.security.auth.common.LogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.filter.CompositeFilter;
import ru.i_novus.ms.audit.client.UserAccessor;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableOAuth2Client
@EnableWebSecurity
@ComponentScan("net.n2oapp.security.admin.auth.server")
@Import(OAuthServerConfiguration.class)
@Order(200)
public class TestAuthGatewayConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${access.auth.login-entry-point:/}")
    private String loginEntryPoint;

    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    @Autowired
    private UserDetailsServiceImpl gatewayUserDetailsService;

    @Autowired
    private List<LogoutHandler> logoutHandlers;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/css/**", "/icons/**", "/fonts/**", "/public/**", "/static/**", "/webjars/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/", "/login**", "/api/**", "/oauth/certs", "/css/**",
                "/icons/**", "/fonts/**", "/public/**", "/static/**", "/webjars/**", "/keycloak_mock/token", "/keycloak_mock/userinfo").permitAll().anyRequest()
                .authenticated().and().exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(loginEntryPoint)).and().logout()
                .logoutSuccessUrl(loginEntryPoint)
                .logoutSuccessHandler(
                        new OAuth2ProviderRedirectLogoutSuccessHandler(logoutHandlers,
                                keycloak().getLogoutUri(), null)).permitAll()
                .and().csrf().disable()
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

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(ssoKeycloakFilter(keycloak(), "/login/keycloak"));
        filter.setFilters(filters);
        return filter;
    }

    private Filter ssoKeycloakFilter(ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        filter.setRestTemplate(template);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        AuthoritiesPrincipalExtractor extractor = new AuthoritiesPrincipalExtractor(gatewayUserDetailsService, "KEYCLOAK");
        tokenServices.setAuthoritiesExtractor(extractor);
        tokenServices.setPrincipalExtractor(extractor);
        filter.setTokenServices(tokenServices);
        return filter;
    }

    @Bean
    public UserAccessor userAccessor() {
        return () -> {
            String userId, userName;
            userId = userName = "UNKNOWN";
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() != null) {
                if (auth.getPrincipal() instanceof net.n2oapp.security.auth.common.User) {
                    net.n2oapp.security.auth.common.User user = (net.n2oapp.security.auth.common.User) auth.getPrincipal();
                    userId = user.getEmail();
                    userName = user.getUsername();
                } else {
                    userId = "" + auth.getPrincipal();
                }
            }
            return new ru.i_novus.ms.audit.client.model.User(userId, userName);
        };
    }

    /**
     * Клиенсткие ресурсы для протокола OAuth2
     */
    @Getter
    static class ClientResources {

        @NestedConfigurationProperty
        private final AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

        @NestedConfigurationProperty
        private final ResourceServerProperties resource = new ResourceServerProperties();

        @Setter
        private String logoutUri;
    }


    @Configuration
    @EnableResourceServer
    public static class ResourceServerConfig extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().antMatchers("/api/info", "/api/api-docs", "/api/swagger**").permitAll()
                    .and().requestMatcher(new OrRequestMatcher(new AntPathRequestMatcher("/api/**"), new AntPathRequestMatcher("/userinfo")))
                    .authorizeRequests().anyRequest().authenticated();
        }
    }
}


