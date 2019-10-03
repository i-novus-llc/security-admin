package net.n2oapp.security.admin;

import net.n2oapp.security.admin.impl.AdminImplConfiguration;
import net.n2oapp.security.admin.web.AdminWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({AdminImplConfiguration.class, AdminWebConfiguration.class})
public class ExampleApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ExampleApplication.class);
    }
}

