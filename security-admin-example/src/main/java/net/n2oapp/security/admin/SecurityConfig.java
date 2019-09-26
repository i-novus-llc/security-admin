package net.n2oapp.security.admin;

import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.auth.common.AuthoritiesPrincipalExtractor;
import net.n2oapp.security.auth.oauth2.OpenIdSecurityConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

@Configuration
@ComponentScan("net.n2oapp.framework.security.auth.oauth2")
public class SecurityConfig extends OpenIdSecurityConfigurerAdapter {

    @Override
    protected void authorize(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry url) {
        url.anyRequest().authenticated();
    }

    @Bean
    public AuthoritiesPrincipalExtractor authoritiesPrincipalExtractor(UserDetailsService userDetailsService) {
        return new AuthoritiesPrincipalExtractor(userDetailsService);
    }
}
