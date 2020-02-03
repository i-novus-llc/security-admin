package net.n2oapp.security.admin;

import net.n2oapp.security.auth.simple.SimpleSecurityConfigurerAdapter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

@Configuration
@ComponentScan("net.n2oapp.framework.security.auth.oauth2")

public class SecurityConfig extends SimpleSecurityConfigurerAdapter {

    public SecurityConfig(DaoAuthenticationProvider daoAuthenticationProvider) {
        super(daoAuthenticationProvider);
    }

    @Override
    protected void authorize(ExpressionUrlAuthorizationConfigurer<HttpSecurity>
                                     .ExpressionInterceptUrlRegistry url) throws Exception {
        url.anyRequest().authenticated().and().headers().frameOptions().disable();
    }
}
