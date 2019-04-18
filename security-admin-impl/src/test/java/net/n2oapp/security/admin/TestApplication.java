package net.n2oapp.security.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication
@EnableEmbeddedPg
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}


