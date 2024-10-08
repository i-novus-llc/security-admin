package net.n2oapp.security.admin.impl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.n2oapp.security.admin.api.model.UserLevel;
import org.hibernate.annotations.Formula;

import java.util.List;

/**
 * Сущность Право доступа
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "permission", schema = "sec")
public class PermissionEntity {

    /**
     * Код права доступа
     */
    @Id
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * Наименование права доступа
     */
    @Column(name = "name", nullable = false)
    private String name;

    @JoinColumn(name = "parent_code")
    @ManyToOne(fetch = FetchType.LAZY)
    private PermissionEntity parentPermission;

    @Formula("(SELECT count(*) != 0 from sec.permission c where c.parent_code = code)")
    private Boolean hasChildren;

    /**
     * Уровень пользователя,  для которого предназначена привилегия
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_level")
    private UserLevel userLevel;

    /**
     * Прикладная система (подсистема, модуль), которой принадлежит привилегия
     */
    @JoinColumn(name = "system_code")
    @ManyToOne(fetch = FetchType.EAGER)
    private SystemEntity systemCode;

    @ManyToMany(mappedBy = "permissionList")
    private List<RoleEntity> roleList;

    public PermissionEntity(String id) {
        setCode(id);
    }
}
