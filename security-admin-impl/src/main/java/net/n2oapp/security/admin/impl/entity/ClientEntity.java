package net.n2oapp.security.admin.impl.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Сущность Клиент
 */
@Entity
@Getter
@Setter
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
     * Время жизни токена доступа (в секундах)
     */
    @Column(name = "access_token_validity_seconds")
    private Integer accessTokenValiditySeconds;

    /**
     * Время жизни Refresh-токена (в секундах)
     */
    @Column(name = "refresh_token_validity_seconds")
    private Integer refreshTokenValiditySeconds;

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
    private List<RoleEntity> roleList;
}
