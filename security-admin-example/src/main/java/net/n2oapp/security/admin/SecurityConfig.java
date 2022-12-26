package net.n2oapp.security.admin;

import net.n2oapp.security.auth.simple.SimpleSecurityCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

@Configuration
public class SecurityConfig extends SimpleSecurityCustomizer {

    public SecurityConfig(DaoAuthenticationProvider daoAuthenticationProvider) {
        super(daoAuthenticationProvider);
    }

    @Override
    protected void configureHttpSecurity(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated().and().headers().frameOptions().disable();
        super.configureHttpSecurity(http);
    }

    @Override
    protected void ignore(WebSecurity.IgnoredRequestConfigurer ignore) {
        super.ignore(ignore);
        ignore.antMatchers("/css/**", "/serviceWorker.js");
    }
}
