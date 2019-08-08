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
     * Адрес возврата после аутентификации
     */
    private String redirectUrl = "http://localhost:8080/admin";

    /**
     * Адрес сервера keycloak
     */
    private String serverUrl = "http://127.0.0.1:8085/auth";

    /**
     * Название домена
     */
    private String realm = "epmp";

    /**
     * Идентификатор клиента client id
     */
    private String clientId = "security-admin-sso";

    /**
     * Идентификатор клиента администрирования пользователей и ролей в keycloak
     */
    private String adminClientId = "admin-cli";

    /**
     * Имя пользователя с правами на создание, реадктирование и удаление ролей и пользователей в keycloak
     */
    private String username = "restclient";

    /**
     * Пароль пользователя с правами на создание, реадктирование и удаление ролей и пользователей в keycloak
     */
    private String password = "restclient";

    /**
     * Отправлять ли подтверждение email при создании пользователя
     */
    private Boolean sendVerifyEmail = true;

    /**
     * Отправлять ли ссылку на смену пароля при создании пользователя
     */
    private Boolean sendChangePassword = true;
}
