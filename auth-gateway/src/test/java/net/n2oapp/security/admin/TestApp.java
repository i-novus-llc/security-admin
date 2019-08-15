package net.n2oapp.security.admin;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication
@EnableEmbeddedPg
public class TestApp {
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }
}
