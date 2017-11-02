package net.n2oapp.security.admin.impl.entity;

import javax.persistence.*;
import java.util.Set;

/**
 * Абстрактная сущность Роли
 * Выделена для возможности переодпределять в прикладных приложениях
 */
@Entity
public abstract class AbstractRoleEntity {
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
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Код роли
     */
    @Column(name = "code", nullable = false)
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
    private Set<PermissionEntity> permissionSet;

    public Set<PermissionEntity> getPermissionSet() {
        return permissionSet;
    }

    public void setPermissionSet(Set<PermissionEntity> permissionSet) {
        this.permissionSet = permissionSet;
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
