package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Сущность Роль
 */
@Entity
@Data
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
     * Права доступа роли
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permission", schema = "sec",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id")}
    )
    private List<PermissionEntity> permissionList;


    @ManyToMany(mappedBy = "roleList")
    private List<UserEntity> userList;


    public RoleEntity(Integer id) {
        setId(id);
    }


}

