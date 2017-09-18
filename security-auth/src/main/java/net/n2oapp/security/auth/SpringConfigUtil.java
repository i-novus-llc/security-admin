package net.n2oapp.security.auth;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Утилита для конфигурации аутентификации spring security
 */
public class SpringConfigUtil {

    public static HttpSecurity cofigureHttp(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()
                .antMatchers("/registration/**", "/registrationServlet/**", "/#index/**","/dist/css/n2o/bootstrap.css",
                        "/dist/css/n2o/n2o.css", "/net/n2oapp/security/auth/css/signin.css", "/favicon.ico").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .failureUrl("/login?error=true")
                .defaultSuccessUrl("/#index")
                .and()
                .logout()
                .logoutUrl("/logout").permitAll()
                .logoutSuccessUrl("/#index")
                .deleteCookies("JSESSIONID")
                .and()
                .headers().contentTypeOptions().disable()
                .and().rememberMe().key("uniqueKey")
                .and().csrf().disable();

    }
}

