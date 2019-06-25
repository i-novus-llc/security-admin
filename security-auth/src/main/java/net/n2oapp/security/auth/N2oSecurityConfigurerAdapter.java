package net.n2oapp.security.auth;

import net.n2oapp.framework.access.simple.PermissionApi;
import net.n2oapp.security.auth.context.SpringSecurityUserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

public abstract class N2oSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Value("${n2o.api.url:/n2o}")
    private String n2oUrl;

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
        ignore.antMatchers("/static/**", "/public/**", "/dist/**", "/webjars/**", "/lib/**", "/build/**", "/bundle/**", "/error");
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

    protected HttpSecurity configureExceptionHandling(ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling) throws Exception {
        return exceptionHandling
                .authenticationEntryPoint(new N2oUrlAuthenticationEntryPoint("/login", n2oUrl))
                .and();
    }
}
