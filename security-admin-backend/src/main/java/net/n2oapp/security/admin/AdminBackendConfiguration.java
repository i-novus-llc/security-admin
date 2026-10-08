package net.n2oapp.security.admin;

import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.rest.api.UserDetailsRestService;
import net.n2oapp.security.admin.rest.impl.UserDetailsRestServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminBackendConfiguration {

    @Bean
    public UserDetailsRestService userDetailsRestService(UserDetailsService userDetailsService) {
        return new UserDetailsRestServiceImpl(userDetailsService);
    }
}
