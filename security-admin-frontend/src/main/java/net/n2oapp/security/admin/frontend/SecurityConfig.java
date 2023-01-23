package net.n2oapp.security.admin.frontend;

import net.n2oapp.security.admin.rest.client.AccountServiceRestClient;
import net.n2oapp.security.auth.OpenIdSecurityCustomizer;
import net.n2oapp.security.auth.common.KeycloakUserService;
import net.n2oapp.security.auth.context.account.ContextFilter;
import net.n2oapp.security.auth.context.account.ContextUserInfoTokenServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
public class SecurityConfig extends OpenIdSecurityCustomizer {

    @Value("${access.service.userinfo-url}")
    private String userInfoUri;

    @Autowired
    private AccountServiceRestClient accountServiceRestClient;
    @Autowired
    private KeycloakUserService keycloakUserService;

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Override
    protected void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests().anyRequest().authenticated().and().oauth2Login().userInfoEndpoint(userInfo -> userInfo.oidcUserService(keycloakUserService));
        ContextUserInfoTokenServices tokenServices = new ContextUserInfoTokenServices(userInfoUri);
        http.addFilterAfter(new ContextFilter(tokenServices, accountServiceRestClient), FilterSecurityInterceptor.class);
    }

    @Override
    protected void ignore(HttpSecurity http) throws Exception {
        super.ignore(http);
    }
}
