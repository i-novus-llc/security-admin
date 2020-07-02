package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Сущность Регион
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "region", schema = "sec")
public class RegionEntity {
    /**
     * Уникальный идентификатор записи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Двухзначный код региона (субъекта) РФ
     */
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * Наименование региона
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Код ОКАТО региона (субъекта РФ)
     */
    @Column(name = "OKATO")
    private String okato;

    /**
     * Признак что запись была удалена из справочника
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    public RegionEntity(Integer id) {
        this.id = id;
    }
}