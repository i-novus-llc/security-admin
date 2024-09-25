package net.n2oapp.security.admin;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.platform.test.autoconfigure.pg.EnableTestcontainersPg;
import net.n2oapp.platform.test.autoconfigure.pg.TestcontainersPgAutoConfiguration;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.rest.api.*;
import net.n2oapp.security.admin.rest.impl.UserDetailsRestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication
@EnableJaxRsProxyClient(
        classes = {UserRestService.class, RoleRestService.class,
                PermissionRestService.class, UserDetailsRestService.class,
                AccountTypeRestService.class, DepartmentRestService.class, UserLevelRestService.class,
                OrganizationRestService.class, SystemRestService.class, OrganizationPersistRestService.class, RegionRestService.class},
        address = "http://localhost:${server.port}/api")
@EnableTestcontainersPg
@Import(TestcontainersPgAutoConfiguration.class)
public class TestApplication {

    @Autowired
    UserDetailsService userDetailsService;

    @Bean
    public UserDetailsRestService UserDetailsRestService() {
        return new UserDetailsRestServiceImpl(userDetailsService);
    }

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}


