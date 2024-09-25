package net.n2oapp.security.admin.impl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность Система
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "system", schema = "sec")
public class SystemEntity {
    /**
     * Код системы (уникальный)
     */
    @Id
    @NotNull
    @Column(name = "code")
    private String code;

    /**
     * Наименование системы
     */
    @NotNull
    @Column(name = "name")
    private String name;

    /**
     * Описание системы
     */
    @Column(name = "description")
    private String description;

    /**
     * Краткое наименование системы
     */
    @Column(name = "short_name ")
    private String shortName;

    /**
     * Иконка системы
     */
    @Column(name = "icon")
    private String icon;

    /**
     * Адрес системы
     */
    @Column(name = "url")
    private String url;

    /**
     * Признак работы в режиме без авторизации
     */
    @Column(name = "public_access")
    private Boolean publicAccess;

    /**
     * Порядок отображения подсистемы
     */
    @Column(name = "view_order")
    private Integer viewOrder;

    /**
     * Отображение системы в едином интерфейсе
     */
    @Column(name = "show_on_interface")
    private Boolean showOnInterface;


    public SystemEntity(@NotNull String code) {
        this.code = code;
    }
}

