package net.n2oapp.security.auth;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

/**
 * Утилита для конфигурации аутентификации spring security
 */
public class SpringConfigUtil {

    public static HttpSecurity cofigureHttp(HttpSecurity http) throws Exception {
        return http
                .exceptionHandling().authenticationEntryPoint(new AjaxAwareLoginUrlAuthenticationEntryPoint("/login"))
                .and()
                .authorizeRequests()
                .antMatchers("/registration/**", "/registrationServlet/**", "/dist/**", "/favicon.ico").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .failureUrl("/login?error=true")
                .defaultSuccessUrl("/")
                .and()
                .logout()
                .logoutUrl("/logout").permitAll()
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID")
                .and()
                .headers().contentTypeOptions().disable()
                .and().rememberMe().key("uniqueKey")
                .and().csrf().disable();
    }

    public static WebSecurity configureWebSecurity(WebSecurity webSecurity) {
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setDefaultRolePrefix("");
        handler.setPermissionEvaluator(new N2oSecurityPermissionEvaluator());
        webSecurity.expressionHandler(handler);
        return webSecurity;
    }

}

