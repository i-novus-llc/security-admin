package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.model.UserStatus;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

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
    private String extUid;

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
     * Уровень пользователя, для которого предназначена роль
     */
    @Column(name = "user_level")
    @Enumerated(EnumType.STRING)
    private UserLevel userLevel;

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
     * Количество ролей у пользователя
     */
    @Formula("(select count(*) from sec.user_role ur where ur.user_id = id)")
    private Integer roleCount;

    /**
     * внешний SSO сервер
     */
    @Column(name = "ext_sys")
    private String extSys;

    /**
     * СНИЛС пользователя
     */
    @Column(name = "snils")
    private String snils;

    /**
     * Регион пользователя (заполняется для регионального уровня пользователей)
     */
    @JoinColumn(name = "region_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private RegionEntity region;

    /**
     * Организация пользователя (заполняется для уровня пользователя - организация)
     */
    @JoinColumn(name = "organization_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private OrganizationEntity organization;

    /**
     * Подразделение пользователя (заполняется для федерального уровня пользователя)
     */
    @JoinColumn(name = "department_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private DepartmentEntity department;

    /**
     * Статус регистрации пользователя
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    /**
     * Идентификатор клиента в системе уведомлений
     */
    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "last_action_date")
    private LocalDateTime lastActionDate;

    @PrePersist
    private void prePersist() {
        setLastActionDate(LocalDateTime.now(Clock.systemUTC()));
    }

    @PreUpdate
    private void preUpdate() {
        setLastActionDate(LocalDateTime.now(Clock.systemUTC()));
    }
}

