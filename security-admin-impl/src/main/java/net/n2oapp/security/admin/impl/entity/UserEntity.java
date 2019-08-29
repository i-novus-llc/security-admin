package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Сущность Пользователь
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "user", schema = "sec")
public class UserEntity {

    /**
     * Идентификатор пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Идентификатор пользователя в сторонних системах
     */
    @Column(name = "ext_uid")
    private UUID extUid;

    /**
     * Логин пользователя
     */
    @NotNull
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * Электронный адрес пользователя
     */
    @Column(name = "email")
    private String email;

    /**
     * Фамилия
     */
    @Column(name = "surname")
    private String surname;

    /**
     * Имя
     */
    @Column(name = "name")
    private String name;

    /**
     * Отчество
     */
    @Column(name = "patronymic")
    private String patronymic;

    /**
     * Пароль пользователя
     */
    @Column(name = "password")
    private String passwordHash;

    /**
     * Активен ли пользователь
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Роли пользователя
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(schema = "sec", name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private List<RoleEntity> roleList;

    /**
     * внешний SSO сервер
     */
    @Column(name = "ext_sys")
    private String extSys;

}

