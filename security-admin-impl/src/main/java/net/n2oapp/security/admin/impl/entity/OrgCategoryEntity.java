package net.n2oapp.security.admin.impl.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Сущность категория организации
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "org_category", schema = "sec")
public class OrgCategoryEntity implements Serializable {
    /**
     * Уникальный идентификатор записи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * Код категории организации
     */
    @Column(name = "code")
    private String code;

    /**
     * Наименование категории организации
     */
    @Column(name = "name")
    private String name;

    /**
     * Краткое описание или расшифровка категории
     */
    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "categories")
    private List<OrganizationEntity> organizationList;

    /**
     * Признак что запись была удалена из справочника
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted;
}
