package net.n2oapp.security.auth.simple;

import net.n2oapp.security.auth.N2oSecurityPermissionEvaluator;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

/**
 * Утилита для конфигурации аутентификации spring security
 */
public class SpringConfigUtil {

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
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID")
                .and().rememberMe().key("uniqueKey").and();
    }

    public static WebSecurity configurePermissionEvaluator(WebSecurity webSecurity) {
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setDefaultRolePrefix("");
        handler.setPermissionEvaluator(new N2oSecurityPermissionEvaluator());
        webSecurity.expressionHandler(handler);
        return webSecurity;
    }

}

