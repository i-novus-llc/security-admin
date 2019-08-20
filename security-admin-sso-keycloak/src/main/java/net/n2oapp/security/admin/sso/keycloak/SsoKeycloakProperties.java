package net.n2oapp.security.admin.sso.keycloak;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки для модуля взаимодействия с keycloak
 */
@Data
@ConfigurationProperties("keycloak")
public class SsoKeycloakProperties {

    /**
     * Адрес сервера keycloak
     */
    private String serverUrl = "http://127.0.0.1:8085/auth";

    /**
     * Название домена
     */
    private String realm = "master";

    /**
     * Идентификатор клиента client id
     */
    private String clientId = "security-admin-sso";

    /**
     * Идентификатор клиента администрирования пользователей и ролей в keycloak
     */
    private String adminClientId = "access-admin";

    /**
     * Ключ клиента администрирования пользователей и ролей в keycloak
     */
    private String adminClientSecret;

    /**
     * Является ли пароль временным
     */
    private Boolean temporaryPassword = true;

    /**
     * Подтвержден ли email пользователя
     */
    private Boolean emailVerified = false;
}
