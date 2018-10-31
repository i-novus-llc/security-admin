package net.n2oapp.security.admin.frontend;

import net.n2oapp.framework.security.auth.oauth2.OpenIdSecurityConfigurerAdapter;
import net.n2oapp.security.auth.N2oUrlAuthenticationEntryPoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

@Configuration
public class SecurityConfig extends OpenIdSecurityConfigurerAdapter {

    public SecurityConfig(N2oUrlAuthenticationEntryPoint n2oUrlAuthenticationEntryPoint) {
        super(n2oUrlAuthenticationEntryPoint);
    }

    @Override
    protected void authorize(ExpressionUrlAuthorizationConfigurer<HttpSecurity>
                                     .ExpressionInterceptUrlRegistry url) {
        url.anyRequest().authenticated();
    }

}
