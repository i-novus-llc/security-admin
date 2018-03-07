package net.n2oapp.security.admin.impl.entity;

import javax.persistence.*;

/**
 * Сущность Право доступа
 */
@Entity
@Table(name = "permission", schema = "sec")
public class PermissionEntity extends AbstractPermissionEntity {

    public PermissionEntity() {
    }
    public PermissionEntity(Integer id) {
        setId(id);
    }
}
