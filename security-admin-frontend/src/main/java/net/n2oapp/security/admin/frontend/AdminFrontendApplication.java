package net.n2oapp.security.admin.frontend;

import net.n2oapp.security.admin.web.AdminWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(AdminWebConfiguration.class)
public class AdminFrontendApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(AdminFrontendApplication.class, args);
    }
}
