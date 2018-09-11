package net.n2oapp.security.admin.frontend;

import net.n2oapp.security.admin.rest.client.RestClientConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(RestClientConfiguration.class)
public class AdminApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application
                .properties("spring.config.location=file:" + System.getProperty("user.home") + "/.n2o/placeholders.properties")
                .sources(AdminApplication.class);
    }
}