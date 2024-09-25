package net.n2oapp.security.admin;

import net.n2oapp.platform.test.autoconfigure.pg.TestcontainersPgAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication
@Import(TestcontainersPgAutoConfiguration.class)
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}


