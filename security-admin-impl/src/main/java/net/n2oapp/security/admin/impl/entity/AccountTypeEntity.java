package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.n2oapp.security.admin.api.model.UserLevel;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "account_type", schema = "sec")
public class AccountTypeEntity {
    /**
     * Уникальный идентификатор записи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Код типа аккаунта
     */
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * Наименование типа аккаунта
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Описание типа аккаунта
     */
    @Column(name = "description")
    private String description;

    /**
     * Наименование типа аккаунта
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_level", nullable = false)
    private UserLevel userLevel;

    /**
     * Статус регистрации
     */
    @Column(name = "status", nullable = false)
    private Boolean status;
}
