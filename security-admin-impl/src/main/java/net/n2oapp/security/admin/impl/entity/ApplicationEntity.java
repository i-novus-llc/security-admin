package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Сущность Приложение
 */
@Entity
@Data
@NoArgsConstructor
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

    public ApplicationEntity(String code) {
        this.code = code;
    }
}
