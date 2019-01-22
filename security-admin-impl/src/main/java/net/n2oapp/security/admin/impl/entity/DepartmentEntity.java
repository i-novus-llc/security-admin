package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.n2oapp.security.admin.api.model.department.Department;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;


/**
 * Сущность Подразделение
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "department", schema = "sec")
public class DepartmentEntity extends AbstractEntity {

    /**
     * Идентификатор
     */
    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Полное наименование
     */
    private String fullName;
    /**
     * Сокращенное наименование
     */
    private String shortName;

    public DepartmentEntity(UUID id) {
        setId(id);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartmentEntity that = (DepartmentEntity) o;
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

    public Department extractModel(){
        Department model = new Department();
        model.setId(this.id);
        model.setFullName(this.fullName);
        model.setShortName(this.shortName);
        model.setLastActionDate(getLastActionDate());
        model.setCreationDate(getCreationDate());
        return model;
    }
}
