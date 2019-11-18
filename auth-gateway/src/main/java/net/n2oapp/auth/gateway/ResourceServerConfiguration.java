package net.n2oapp.auth.gateway;

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
        http.requestMatcher(new OrRequestMatcher(
                new AntPathRequestMatcher("/userinfo"),
                new AntPathRequestMatcher("/api/systems/**"),
                new AntPathRequestMatcher("/api/applications/**"),
                new AntPathRequestMatcher("/api/clients/**"),
                new AntPathRequestMatcher("/api/department/**"),
                new AntPathRequestMatcher("/api/organization/**"),
                new AntPathRequestMatcher("/api/permissions/**"),
                new AntPathRequestMatcher("/api/region/**"),
                new AntPathRequestMatcher("/api/roles/**"),
                new AntPathRequestMatcher("/api/userLevels/**"),
                new AntPathRequestMatcher("/api/users/**")
        )).authorizeRequests().anyRequest().authenticated();
    }
}
