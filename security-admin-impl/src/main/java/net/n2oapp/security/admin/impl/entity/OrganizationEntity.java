package net.n2oapp.security.admin.impl.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.n2oapp.security.admin.impl.entity.base.RdmBaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Сущность Организация
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "organization", schema = "sec")
public class OrganizationEntity extends RdmBaseEntity implements Serializable {
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
     * Идентификатор во внешней системе
     */
    @Column(name = "ext_uid")
    private String extUid;

    /**
     * Категории организации
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "assigned_org_category", schema = "sec",
            joinColumns = {@JoinColumn(name = "org_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "org_category_id", referencedColumnName = "id")}
    )
    private List<OrgCategoryEntity> categories;

    /**
     * Роли
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "org_role", schema = "sec",
            joinColumns = {@JoinColumn(name = "org_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private List<RoleEntity> roleList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organization")
    private List<AccountEntity> accounts;

    public OrganizationEntity(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationEntity that = (OrganizationEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
