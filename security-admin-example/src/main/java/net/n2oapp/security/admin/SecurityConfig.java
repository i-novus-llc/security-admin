package net.n2oapp.security.admin;

import net.n2oapp.security.auth.simple.SimpleSecurityCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig extends SimpleSecurityCustomizer {

    public SecurityConfig(DaoAuthenticationProvider daoAuthenticationProvider) {
        super(daoAuthenticationProvider);
    }

    @Override
    protected void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests().anyRequest().authenticated().and().headers().frameOptions().disable();
    }

    @Override
    protected void ignore(HttpSecurity http) throws Exception {
        super.ignore(http);
    }
}
