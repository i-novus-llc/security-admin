package net.n2oapp.security.auth;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class SpringConfigUtil {

    public static HttpSecurity cofigureHttp(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()
                .antMatchers("/registration/**", "/registrationServlet/**", "/#index/**").permitAll()
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

