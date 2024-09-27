package net.n2oapp.security.admin.impl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.n2oapp.security.admin.impl.entity.base.RdmBaseEntity;

/**
 * Сущность Департамент
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "department", schema = "sec")
public class DepartmentEntity extends RdmBaseEntity {
    /**
     * Уникальный идентификатор записи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Код департамента МПС
     */
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * Наименование департамента МПС
     */
    @Column(name = "name", nullable = false)
    private String name;

    public DepartmentEntity(Integer id) {
        this.id = id;
    }
}
