package net.n2oapp.security.admin.frontend;

import net.n2oapp.security.auth.common.GatewayPrincipalExtractor;
import net.n2oapp.security.admin.rest.client.AdminRestClientConfiguration;
import net.n2oapp.security.admin.web.AdminWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
@Import({AdminWebConfiguration.class, AdminRestClientConfiguration.class})
public class AdminFrontendApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(AdminFrontendApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AdminFrontendApplication.class);
    }

    @Bean
    @Primary
    public GatewayPrincipalExtractor gatewayPrincipalExtractor() {
        return new GatewayPrincipalExtractor();
    }
}