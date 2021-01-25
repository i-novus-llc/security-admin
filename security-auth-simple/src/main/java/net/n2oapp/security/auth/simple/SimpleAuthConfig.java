package net.n2oapp.security.auth.simple;

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
public class SimpleAuthConfig {

    @Bean
    public ProviderManager providerManager(DaoAuthenticationProvider authenticationProvider) {
        return new ProviderManager(Arrays.asList(authenticationProvider));
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(@Lazy UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public N2oSimpleDetailManager n2oSimpleDetailManager(DaoAuthenticationProvider authenticationProvider) {
        N2oSimpleDetailManager n2oSimpleDetailManager = new N2oSimpleDetailManager();
        n2oSimpleDetailManager.setAuthenticationProvider(authenticationProvider);
        return n2oSimpleDetailManager;
    }

    @Bean
    public CurrentUserUpdatePasswordService currentUserUpdatePasswordService() {
        return new CurrentUserUpdatePasswordService();
    }

    @Bean
    public RegistrationServlet registrationServlet() {
        return new RegistrationServlet();
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        return new ServletRegistrationBean(registrationServlet(), "/registrationServlet");
    }
}
