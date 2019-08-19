package net.n2oapp.security.admin.frontend;

import net.n2oapp.framework.security.auth.oauth.gateway.GatewayResourceExtractor;
import net.n2oapp.security.auth.OpenIdSecurityConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

@Configuration
public class SecurityConfig extends OpenIdSecurityConfigurerAdapter {

    @Override
    protected void authorize(ExpressionUrlAuthorizationConfigurer<HttpSecurity>
                                     .ExpressionInterceptUrlRegistry url) {
        url.anyRequest().authenticated();
    }

    @Bean
    public GatewayResourceExtractor gatewayResourceExtractor() {
        return new GatewayResourceExtractor();
    }

}
