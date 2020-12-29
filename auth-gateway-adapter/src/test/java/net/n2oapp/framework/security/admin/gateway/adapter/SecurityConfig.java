package net.n2oapp.framework.security.admin.gateway.adapter;

import net.n2oapp.security.auth.common.GatewayPrincipalExtractor;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;

@Configuration
@Import(LogoutConfiguration.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/backchannel_logout").permitAll().anyRequest().authenticated().and().csrf().disable();
    }

    @Bean
    public ServletRegistrationBean<BackChannelLogoutServlet> servletRegistrationBean(SessionRegistry sessionRegistry, JwtVerifier jwtVerifier) {
        return new ServletRegistrationBean<>(new BackChannelLogoutServlet(sessionRegistry, jwtVerifier), "/backchannel_logout");
    }

    @Bean
    @Primary
    public GatewayPrincipalExtractor gatewayPrincipalExtractor() {
        return new GatewayPrincipalExtractor();
    }
}
