package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Сущность Организация
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "organization", schema = "sec")
public class OrganizationEntity {
    /**
     * Уникальный идентификатор записи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Код организации
     */
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * Краткое наименование организации
     */
    @Column(name = "short_name", nullable = false)
    private String shortName;

    /**
     * Код ОГРН (уникальный код организации)
     */
    @Column(name = "OGRN")
    private String ogrn;

    /**
     * Код ОКПО (используется в стат.формах)
     */
    @Column(name = "OKPO")
    private String okpo;

    /**
     * Полное наименование организации
     */
    @Column(name = "full_name")
    private String fullName;

    public OrganizationEntity(Integer id) {
        this.id = id;
    }
}
