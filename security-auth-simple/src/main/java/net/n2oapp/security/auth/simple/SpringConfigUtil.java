package net.n2oapp.security.auth.simple;

import net.n2oapp.security.auth.N2oSecurityPermissionEvaluator;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

/**
 * Утилита для конфигурации аутентификации spring security
 */
public class SpringConfigUtil {

    public static HttpSecurity configureExceptionHandling(ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling) throws Exception {
        return exceptionHandling
                .authenticationEntryPoint(new AjaxAwareLoginUrlAuthenticationEntryPoint("/login"))
                .and();
    }

    public static ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry configureAuthorizeAuthRequests(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry url) throws Exception {
        return url.antMatchers("/registration/**", "/registrationServlet/**", "/dist/**", "/favicon.ico").permitAll();
    }

    public static HttpSecurity configureLogin(FormLoginConfigurer<HttpSecurity> login) {
        return login.loginPage("/login").permitAll()
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .failureUrl("/login?error=true")
                .defaultSuccessUrl("/")
                .and();

    }

    public static HttpSecurity configureLogout(LogoutConfigurer<HttpSecurity> logout) throws Exception {
        return logout.logoutUrl("/logout").permitAll()
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .and().rememberMe().key("uniqueKey").and();
    }


    public static HttpSecurity configureHttp(HttpSecurity http) throws Exception {
        configureExceptionHandling(http.exceptionHandling());
        configureAuthorizeAuthRequests(http.authorizeRequests());
        configureLogin(http.formLogin());
        configureLogout(http.logout());
        http.headers().contentTypeOptions().disable();
        http.csrf().disable();
        return http;
    }


    public static WebSecurity configurePermissionEvaluator(WebSecurity webSecurity) {
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setDefaultRolePrefix("");
        handler.setPermissionEvaluator(new N2oSecurityPermissionEvaluator());
        webSecurity.expressionHandler(handler);
        return webSecurity;
    }

}

