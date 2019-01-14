package net.n2oapp.security.admin.impl.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Сущность Уполномоченные лица Банк
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "employee_bank", schema = "sec")
public class EmployeeBankEntity extends AbstractEntity {

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
     * Банк
     */
    @ManyToOne
    @JoinColumn(name = "bank_id", referencedColumnName = "id")
    private BankEntity bank;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeBankEntity that = (EmployeeBankEntity) o;
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

