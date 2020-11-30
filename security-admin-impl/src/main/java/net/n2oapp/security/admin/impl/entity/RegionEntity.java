package net.n2oapp.security.admin.impl.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Сущность Регион
 */
@Entity
@Getter
@Setter
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "region", cascade = CascadeType.REMOVE)
    private List<UserEntity> users;

    public RegionEntity(Integer id) {
        this.id = id;
    }
}
