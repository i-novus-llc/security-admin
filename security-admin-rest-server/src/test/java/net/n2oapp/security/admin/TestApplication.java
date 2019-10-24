package net.n2oapp.security.admin;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.rest.api.*;
import net.n2oapp.security.admin.rest.impl.UserDetailsRestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication
@EnableJaxRsProxyClient(
        classes = {UserRestService.class, RoleRestService.class, PermissionRestService.class, ClientRestService.class, UserDetailsRestService.class},
        address = "http://localhost:${server.port}/api")
@EnableEmbeddedPg
public class TestApplication {

    @Autowired
    @Qualifier("UserDetailsServiceImpl")
    UserDetailsService userDetailsService;

    @Bean
    public UserDetailsRestService UserDetailsRestService() {
        return new UserDetailsRestServiceImpl(userDetailsService);
    }

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}


