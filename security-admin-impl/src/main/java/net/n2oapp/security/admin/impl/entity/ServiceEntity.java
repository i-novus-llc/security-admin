package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Сущность Служба
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "service", schema = "sec")
public class ServiceEntity {
    /**
     * Код службы
     */
    @Id
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * Наименование службы
     */
    @Column(name = "name", nullable = false)
    private String name;


    /**
     * Прикладная система (подсистема, модуль), которой принадлежит служба
     */
    @JoinColumn(name = "system_code")
    @ManyToOne(fetch = FetchType.EAGER)
    private SystemEntity systemCode;

    public ServiceEntity(String code) {
        this.code = code;
    }
}
