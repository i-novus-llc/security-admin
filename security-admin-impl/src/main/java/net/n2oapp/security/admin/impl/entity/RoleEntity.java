package net.n2oapp.security.admin.impl.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.n2oapp.security.admin.api.model.UserLevel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Сущность Роль
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "role", schema = "sec")
public class RoleEntity {
    /**
     * Идентификатор роли
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Наименование роли
     */
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Код роли
     */
    @Column(name = "code")
    private String code;

    /**
     * Описание роли
     */
    @Column(name = "description")
    private String description;

    /**
     * Уровень пользователя, для которого предназначена роль
     */
    @Column(name = "user_level")
    @Enumerated(EnumType.STRING)
    private UserLevel userLevel;

    /**
     * Прикладная система (подсистема, модуль), которой принадлежит роль
     */
    @JoinColumn(name = "system_code")
    @ManyToOne(fetch = FetchType.EAGER)
    private SystemEntity systemCode;

    /**
     * Права доступа роли
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permission", schema = "sec",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_code")}
    )
    private List<PermissionEntity> permissionList;


    @ManyToMany(mappedBy = "roleList")
    private List<AccountEntity> accountList;

    @ManyToMany(mappedBy = "roleList")
    private List<ClientEntity> clientList;

    @OneToMany(mappedBy = "id.role")
    private List<AccountTypeRoleEntity> accountTypeRoleList;

    public RoleEntity(Integer id) {
        setId(id);
    }


}

