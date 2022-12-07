package net.n2oapp.security.admin.frontend;

import net.n2oapp.security.account.context.ContextFilter;
import net.n2oapp.security.account.context.ContextUserInfoTokenServices;
import net.n2oapp.security.admin.rest.client.AccountServiceRestClient;
import net.n2oapp.security.auth.OpenIdSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

@Configuration
public class SecurityConfig extends OpenIdSecurityConfigurerAdapter {

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${access.service.userinfo-url}")
    private String userInfoUri;

    @Autowired
    private AccountServiceRestClient accountServiceRestClient;

    @Override
    protected void authorize(ExpressionUrlAuthorizationConfigurer<HttpSecurity>
                                     .ExpressionInterceptUrlRegistry url) {
        url.anyRequest().authenticated();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        ContextUserInfoTokenServices tokenServices = new ContextUserInfoTokenServices(userInfoUri, clientId);
        http.addFilterAfter(new ContextFilter(tokenServices, accountServiceRestClient), FilterSecurityInterceptor.class);
    }

    @Override
    protected void ignore(WebSecurity.IgnoredRequestConfigurer ignore) {
        super.ignore(ignore);
        ignore.antMatchers("/css/**");
    }
}
