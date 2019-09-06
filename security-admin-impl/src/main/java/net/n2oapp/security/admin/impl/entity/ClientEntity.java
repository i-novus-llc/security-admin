package net.n2oapp.security.admin.impl.entity;


import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

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
     * Разрешенные клиенту типы авторизации
     */
    @Column(name = "grant_types")
    private String grantTypes;

    /**
     * Ссылки разрешённые для редиректа
     */
    @Column(name = "redirect_uris")
    private String redirectUris;

    /**
     * Время жизни токена доступа
     */
    @Column(name = "access_token_lifetime")
    private Integer accessTokenLifetime;

    /**
     * Время жизни refresh токена
     */
    @Column(name = "refresh_token_lifetime")
    private Integer refreshTokenLifetime;

    /**
     * Ссылка для выхода
     */
    @Column(name = "logout_url")
    private String logoutUrl;

    /**
     * Роли клиента
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(schema = "sec", name = "client_role",
            joinColumns = {@JoinColumn(name = "client_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private List<RoleEntity> roles;

}
