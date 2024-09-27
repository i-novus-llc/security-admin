package net.n2oapp.security.admin;

import net.n2oapp.platform.test.autoconfigure.pg.EnableTestcontainersPg;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication
@EnableTestcontainersPg
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}


