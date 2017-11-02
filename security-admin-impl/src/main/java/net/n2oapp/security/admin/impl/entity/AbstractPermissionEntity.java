package net.n2oapp.security.admin.impl.entity;

import javax.persistence.*;

/**
 * Абстрактная сущность Права доступа
 * Выделена для возможности переодпределять в прикладных приложениях
 */
@Entity
public abstract class AbstractPermissionEntity {
    /**
     * Идентификатор права доступа
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Наименование права доступа
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Код права доступа
     */
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "parent_id")
    private Integer parentId;

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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
