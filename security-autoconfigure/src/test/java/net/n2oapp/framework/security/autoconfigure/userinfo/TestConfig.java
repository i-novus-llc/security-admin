package net.n2oapp.framework.security.autoconfigure.userinfo;

import net.n2oapp.framework.security.autoconfigure.userinfo.mapper.OauthPrincipalToJsonMapper;
import net.n2oapp.framework.security.autoconfigure.userinfo.mapper.PrincipalToJsonAbstractMapper;
import net.n2oapp.security.auth.common.OauthUser;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@TestConfiguration
public class TestConfig {

    @RestController
    @RequestMapping
    @UserInfo
    public static class TestController {
        @GetMapping("/")
        @UserInfo
        public Boolean testEndpoint() {
            return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserInfoModel;
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Primary
    public PrincipalToJsonAbstractMapper<OauthUser> oauthPrincipalToJsonMapper() {
        return new OauthPrincipalToJsonMapper();
    }
}
