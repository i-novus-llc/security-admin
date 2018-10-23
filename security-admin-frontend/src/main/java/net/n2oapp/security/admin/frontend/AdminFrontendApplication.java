package net.n2oapp.security.admin.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class AdminFrontendApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(AdminFrontendApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AdminFrontendApplication.class);
    }
}