package net.n2oaap.security.admin.sso.keycloak;

import net.n2oapp.security.admin.sso.keycloak.AdminSsoKeycloakProperties;
import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.inovus.ms.rdm.sync.RdmClientSyncAutoConfiguration;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication(exclude = RdmClientSyncAutoConfiguration.class)
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