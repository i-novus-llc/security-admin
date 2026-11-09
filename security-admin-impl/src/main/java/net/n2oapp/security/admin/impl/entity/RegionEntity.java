package net.n2oapp.security.admin.impl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.n2oapp.security.admin.impl.entity.base.RdmBaseEntity;

import java.io.Serializable;

/**
 * Сущность Регион
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "region", schema = "sec")
public class RegionEntity extends RdmBaseEntity implements Serializable {
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

    public RegionEntity(Integer id) {
        this.id = id;
    }
}
