package net.n2oapp.security.admin;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.security.admin.rest.api.PermissionRestService;
import net.n2oapp.security.admin.rest.api.RoleRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication
@EnableJaxRsProxyClient(
        classes = {UserRestService.class, RoleRestService.class, PermissionRestService.class},
        address = "http://localhost:${server.port}/rest")
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}


