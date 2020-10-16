package net.n2oapp.security.admin.sso.keycloak;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки для модуля взаимодействия с keycloak
 */
@Getter
@Setter
@ConfigurationProperties("access.keycloak")
public class AdminSsoKeycloakProperties {

    /**
     * Адрес сервера keycloak
     */
    private String serverUrl = "http://127.0.0.1:8085/auth";

    /**
     * Название домена
     */
    private String realm = "master";

    /**
     * Идентификатор клиента для синхронизации
     */
    private String adminClientId = "access-admin";

    /**
     * Секретное слово клиента для синхронизации
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

    /**
     * Cron выражение частоты синхронизации пользователей
     */
    private String synchronizeFrequency = "0 0/30 * * * ? *";

    /**
     * Включение автоматической синхронизации пользователей
     */
    private Boolean synchronizeEnabled = false;

    /**
     * По сколько пользователей обрабатывать за один раз
     */
    private Integer synchronizeUserCount = 100;
}
