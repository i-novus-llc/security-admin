package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Сущность Организация
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "organization", schema = "sec")
public class OrganizationEntity implements Serializable {
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

    /**
     * ИНН организации
     */
    @Column(name = "inn")
    private String inn;

    /**
     * КПП организации
     */
    @Column(name = "kpp")
    private String kpp;

    /**
     * Юридический адрес
     */
    @Column(name = "legal_address")
    private String legalAddress;

    /**
     * Электронная почта
     */
    @Column(name = "email")
    private String email;

    /**
     * Категории организации
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "assigned_org_category", schema = "sec",
            joinColumns = {@JoinColumn(name = "org_code", referencedColumnName = "code")},
            inverseJoinColumns = {@JoinColumn(name = "org_category_code", referencedColumnName = "code")}
    )
    private List<OrgCategoryEntity> categories;

    /**
     * Признак что запись была удалена из справочника
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    public OrganizationEntity(Integer id) {
        this.id = id;
    }
}
