package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Сущность Департамент
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "department", schema = "sec")
public class DepartmentEntity {
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

    /**
     * Признак что запись была удалена из справочника
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    public DepartmentEntity(Integer id) {
        this.id = id;
    }
}
