package net.n2oaap.security.admin.sso.keycloak;

import net.n2oapp.platform.test.autoconfigure.pg.EnableTestcontainersPg;
import net.n2oapp.platform.test.autoconfigure.pg.TestcontainersPgAutoConfiguration;
import net.n2oapp.security.admin.sso.keycloak.AdminSsoKeycloakProperties;
import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication
@EnableTestcontainersPg
@Import(TestcontainersPgAutoConfiguration.class)
public class TestApplication {

    @Bean
    public SpringBus cxf() {
        return new SpringBus();
    }

    @Bean
    public AdminSsoKeycloakProperties adminSsoKeycloakProperties() {
        return new AdminSsoKeycloakProperties();
    }

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}