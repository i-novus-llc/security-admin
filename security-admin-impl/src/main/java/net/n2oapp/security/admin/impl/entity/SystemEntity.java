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
    @NotNull
    @Column(name = "description")
    private String description;

    /**
     * Службы системы
     */
    @OneToMany(mappedBy = "systemCode", fetch = FetchType.LAZY)
    private List<ServiceEntity> serviceList;


    public SystemEntity(@NotNull String code) {
        this.code = code;
    }
}

