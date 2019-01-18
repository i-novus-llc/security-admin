package net.n2oapp.security.admin.impl.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

/**
 * Сущность Уполномоченные лица ДОМ.РФ
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "employee_domrf", schema = "sec")
public class EmployeeDomrfEntity extends AbstractEntity {

    /**
     * Идентификатор записи
     */
    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Должность
     */
    @Column(name = "position")
    private String position;

    /**
     * Пользователь
     */

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    /**
     * Подразделение
     */
    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private DepartmentEntity department;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeDomrfEntity that = (EmployeeDomrfEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

}

