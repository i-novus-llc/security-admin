package net.n2oapp.security.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import ru.i_novus.ms.audit.client.AuditClient;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication
@EnableEmbeddedPg
public class TestApplication {

    @MockBean
    AuditClient auditClient;

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}


