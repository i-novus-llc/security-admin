package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * Сущность Право доступа
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "permission", schema = "sec")
public class PermissionEntity {
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


    @ManyToMany(mappedBy = "permissionList")
    private List<RoleEntity> roleList;


    public PermissionEntity(Integer id) {
        setId(id);
    }



}
