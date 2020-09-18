package net.n2oapp.framework.security.admin.gateway.adapter;

import net.n2oapp.framework.security.auth.oauth2.gateway.GatewayPrincipalExtractor;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;

@Configuration
@Import(LogoutConfiguration.class)
@ComponentScan("net.n2oapp.framework.security.auth.oauth2")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll().and().csrf().disable();
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(SessionRegistry sessionRegistry, JwtVerifier jwtVerifier) {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new BackChannelLogoutServlet(sessionRegistry, jwtVerifier), "/backchannel_logout");
        return servletRegistrationBean;
    }

    @Bean
    @Primary
    public GatewayPrincipalExtractor gatewayPrincipalExtractor() {
        return new GatewayPrincipalExtractor();
    }
}
