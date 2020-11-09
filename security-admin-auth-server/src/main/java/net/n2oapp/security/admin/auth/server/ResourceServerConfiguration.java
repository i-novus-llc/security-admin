package net.n2oapp.security.admin.auth.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/api/info", "/api/api-docs", "/api/swagger**").permitAll()
                .and().requestMatcher(new OrRequestMatcher(new AntPathRequestMatcher("/api/**"), new AntPathRequestMatcher("/userinfo")))
                .authorizeRequests().anyRequest().authenticated();
    }
}