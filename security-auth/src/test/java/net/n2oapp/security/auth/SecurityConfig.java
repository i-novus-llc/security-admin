package net.n2oapp.security.auth;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@TestConfiguration
public class SecurityConfig extends N2oSecurityCustomizer {

    @Override
    protected void configureHttpSecurity(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authManagerRequest -> authManagerRequest.anyRequest().authenticated());
    }
}
