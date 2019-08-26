package net.n2oapp.security.admin.impl.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 * Сущность Клиент
 */

@Entity
@Data
@Table(name = "client", schema = "sec")
public class ClientEntity {


    /**
     * Имя клиента
     */
    @Id
    @NotBlank
    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;

    /**
     * Секрет клиента
     */
    @Column(name = "client_secret")
    private String clientSecret;

    /**
     * Тип авторизации разрешённой клиента
     */
    @Column(name = "authorized_grant_types")
    private String authorizedGrantTypes;

    /**
     * Ссылки разрешённые для редиректа
     */
    @Column(name = "registered_redirect_uri")
    private String registeredRedirectUri;

    /**
     * Время жизни токена
     */
    @Column(name = "access_token_validity_seconds")
    private Integer accessTokenValiditySeconds;

    /**
     * Время жизни refresh токена
     */
    @Column(name = "refresh_token_validity_seconds")
    private Integer refreshTokenValiditySeconds;

    /**
     * Ссылка для выхода
     */
    @Column(name = "logout_url")
    private String logoutUrl;

}
