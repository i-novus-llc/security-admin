package net.n2oapp.security.admin.impl.entity;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.security.admin.api.model.UserStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сущность Пользователь
 */
@Entity
@Getter
@Setter
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
     * Аккаунты пользователя
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<AccountEntity> accounts;

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
     * СНИЛС пользователя
     */
    @Column(name = "snils")
    private String snils;

    /**
     * Статус регистрации пользователя
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    /**
     * Дата и время последних изменений
     */
    @Column(name = "last_action_date")
    private LocalDateTime lastActionDate;

    /**
     * Срок действия учётной записи пользователя
     */
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @PrePersist
    @PreUpdate
    private void preUpdate() {
        setLastActionDate(LocalDateTime.now(Clock.systemUTC()));
    }
}

