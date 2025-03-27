package net.n2oapp.framework.security.autoconfigure.access;

import net.n2oapp.security.admin.rest.client.AccountServiceRestClient;
import net.n2oapp.security.admin.rest.client.AdminRestClientConfiguration;
import net.n2oapp.security.auth.OpenIdSecurityCustomizer;
import net.n2oapp.security.auth.context.account.ContextFilter;
import net.n2oapp.security.auth.context.account.ContextUserInfoTokenServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@ConditionalOnProperty(value = "access.security-autoconfigure.disabled", matchIfMissing = true , havingValue = "false")
@ConditionalOnMissingBean(type = "net.n2oapp.security.auth.OpenIdSecurityCustomizer")
@ConditionalOnClass(name = "net.n2oapp.security.auth.N2oSecurityCustomizer")
@AutoConfiguration
@Import(AdminRestClientConfiguration.class)
@AutoConfigureBefore(name = "net.n2oapp.framework.boot.N2oFrameworkAutoConfiguration")
@PropertySource("classpath:/access.properties")
public class SecurityAutoConfiguration extends OpenIdSecurityCustomizer {

    @Autowired
    private AccountServiceRestClient accountServiceRestClient;
    @Autowired
    private OAuth2UserService<OidcUserRequest, OidcUser> keycloakUserService;
    @Autowired
    private ContextUserInfoTokenServices tokenServices;

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    protected SecurityPropertyInformer securityPropertyInformer() {
        return new SecurityPropertyInformer();
    }

    @Override
    protected void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeHttpRequests(authz -> authz.anyRequest().authenticated()).oauth2Login(oauth2 -> oauth2.userInfoEndpoint(userInfo -> userInfo.oidcUserService(keycloakUserService)));
        http.addFilterAfter(new ContextFilter(tokenServices, accountServiceRestClient), AuthorizationFilter.class);
    }
}

