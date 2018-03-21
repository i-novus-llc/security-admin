package net.n2oapp.security.admin.impl.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Сущность Роль
 */
@Entity
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

    public List<UserEntity> getUserList() {
        return userList;
    }

    public void setUserList(List<UserEntity> userList) {
        this.userList = userList;
    }

    public RoleEntity() {
    }

    public RoleEntity(Integer id) {
        setId(id);
    }

    public List<PermissionEntity> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<PermissionEntity> permissionList) {
        this.permissionList = permissionList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

