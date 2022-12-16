package net.n2oapp.security.admin;

import net.n2oapp.security.admin.impl.AdminImplConfiguration;
import net.n2oapp.security.admin.rest.impl.AdminRestServerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class/*, ManagementWebSecurityAutoConfiguration.class*/})
@Import({AdminRestServerConfiguration.class, AdminImplConfiguration.class})
public class AdminBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminBackendApplication.class, args);
    }
}
