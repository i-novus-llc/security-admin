package net.n2oapp.security.admin.impl.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Сущность Приложение
 */
@Entity
@Getter
@Setter
@Table(name = "application", schema = "sec")
public class ApplicationEntity {
    /**
     * Код приложения
     */
    @Id
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * Наименование приложения
     */
    @Column(name = "name", nullable = false)
    private String name;


    /**
     * Прикладная система (подсистема, модуль), которой принадлежит приложение
     */
    @JoinColumn(name = "system_code")
    @ManyToOne(fetch = FetchType.EAGER)
    private SystemEntity systemCode;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "code", referencedColumnName = "client_id")
    private ClientEntity client;
}
