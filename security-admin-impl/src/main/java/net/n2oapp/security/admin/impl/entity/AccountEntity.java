package net.n2oapp.security.admin.impl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.n2oapp.security.admin.api.model.UserLevel;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "account", schema = "sec")
public class AccountEntity {

    /**
     * Уникальный идентификатор записи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Пользователь, которому принадлежит аккаунт
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /**
     * Имя аккаунта
     */
    private String name;

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
     * Уровень пользователя, для которого предназначена роль
     */
    @Column(name = "user_level")
    @Enumerated(EnumType.STRING)
    private UserLevel userLevel;

    /**
     * Активен ли данный аккаунт
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Роли пользователя
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(schema = "sec", name = "account_role",
            joinColumns = {@JoinColumn(name = "account_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private List<RoleEntity> roleList;

    /**
     * внешний SSO сервер
     */
    @Column(name = "external_system")
    private String externalSystem;

    /**
     * Идентификатор пользователя в сторонних системах
     */
    @Column(name = "external_uid")
    private String externalUid;
}
