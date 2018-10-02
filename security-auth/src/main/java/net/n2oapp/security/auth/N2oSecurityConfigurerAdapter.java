package net.n2oapp.security.auth;

import net.n2oapp.framework.access.AdminService;
import net.n2oapp.framework.access.simple.PermissionApi;
import net.n2oapp.framework.access.simple.SimpleAuthorizationApi;
import net.n2oapp.framework.api.metadata.pipeline.ReadCompileBindTerminalPipeline;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.security.auth.context.SpringSecurityUserContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import java.util.Collections;

public abstract class N2oSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Bean
    public PermissionApi securitySimplePermissionApi() {
        return new SecuritySimplePermissionApi();
    }

    @Bean
    public SpringSecurityUserContext springSecurityUserContext() {
        return new SpringSecurityUserContext();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        ignore(web.ignoring());
    }

    protected void ignore(WebSecurity.IgnoredRequestConfigurer ignore) {
        ignore.antMatchers("/static/**", "/public/**", "/dist/**", "/webjars/**", "/lib/**", "/build/**", "/bundle/**");
    }

    protected abstract void authorize(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry url)
            throws Exception;

    protected ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry beforeAuthorize(HttpSecurity http)
            throws Exception {
        return http.authorizeRequests();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        authorize(beforeAuthorize(http));
    }
}
