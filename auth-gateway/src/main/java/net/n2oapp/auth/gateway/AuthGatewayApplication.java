package net.n2oapp.auth.gateway;

import net.n2oapp.security.admin.auth.server.AuthGatewayConfiguration;
import net.n2oapp.security.admin.impl.AdminImplConfiguration;
import net.n2oapp.security.admin.rest.impl.AdminRestServerConfiguration;
import net.n2oapp.security.admin.sso.keycloak.SsoKeycloakConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({AdminRestServerConfiguration.class, AdminImplConfiguration.class,
        SsoKeycloakConfiguration.class, AuthGatewayConfiguration.class})
public class AuthGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthGatewayApplication.class, args);
    }
}
