package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


/**
 * Сущность Банк
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "bank")
public class BankEntity extends AbstractEntity {

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
    /**
     * Регистрационный номер
     */
    private String regNum;
    /**
     * Дата регистрации Банком России
     */
    private LocalDateTime regDt;
    /**
     * ИНН
     */
    private String inn;

    /**
     * ОГРН
     */
    private String ogrn;

    /**
     * БИК
     */
    private String bik;

    /**
     * КПП
     */
    private String kpp;

    /**
     * Юридический адрес
     */
    private String legalAddress;

    /**
     * Фактический адрес
     */
    private String actualAddress;

    /**
     * Головная организация
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private BankEntity parent;

    public BankEntity(UUID id) {
        setId(id);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankEntity that = (BankEntity) o;
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
