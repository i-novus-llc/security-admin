package net.n2oapp.framework.security.auth.oauth2;

import net.n2oapp.security.auth.N2oUrlAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
public class OAuth2Config {

    @Value("${n2o.api.url}")
    private String n2oUrl;

    @Bean
    public N2oUrlAuthenticationEntryPoint n2oUrlAuthenticationEntryPoint() {
        return new N2oUrlAuthenticationEntryPoint("/login", n2oUrl);
    }

}
