package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Сущность Система
 */
@Entity
@Data
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
     * Приложения системы
     */
    @OneToMany(mappedBy = "systemCode", fetch = FetchType.LAZY)
    private List<ApplicationEntity> applicationList;


    public SystemEntity(@NotNull String code) {
        this.code = code;
    }
}

