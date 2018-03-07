package net.n2oapp.security.admin.impl.entity;

import javax.persistence.*;

/**
 * Сущность Роль
 */
@Entity
@Table(name = "role", schema = "sec")
public class RoleEntity extends AbstractRoleEntity {

    public RoleEntity() {
    }

    public RoleEntity(Integer id) {
        setId(id);
    }
}

