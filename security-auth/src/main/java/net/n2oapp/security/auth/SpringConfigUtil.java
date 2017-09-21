package net.n2oapp.security.auth;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Утилита для конфигурации аутентификации spring security
 */
public class SpringConfigUtil {

    public static HttpSecurity cofigureHttp(HttpSecurity http) throws Exception {
        return http
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
}

