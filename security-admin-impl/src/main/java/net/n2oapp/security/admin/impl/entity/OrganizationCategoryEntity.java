package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Сущность категория организации
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "org_category", schema = "sec")
public class OrganizationCategoryEntity {
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
}
