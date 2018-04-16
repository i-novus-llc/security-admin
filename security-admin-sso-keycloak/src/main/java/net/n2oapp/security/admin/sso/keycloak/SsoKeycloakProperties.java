package net.n2oapp.security.admin.sso.keycloak;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки для модуля взаимодействия с keycloak
 */
@Data
@ConfigurationProperties("keycloak")
public class SsoKeycloakProperties {

    private String serverUrl = "http://127.0.0.1:8080/auth";
    private String realm = "security-admin";
    private String clientId = "admin-cli";
    private String username = "restadmin";
    private String password;
}
