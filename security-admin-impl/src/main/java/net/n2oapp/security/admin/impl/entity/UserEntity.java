package net.n2oapp.security.admin.impl.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import net.n2oapp.security.admin.api.model.User;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Сущность Пользователь
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "user", schema = "sec")
public class UserEntity extends AbstractEntity {

    /**
     * Идентификатор пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Идентификатор пользователя в сторонних системах
     */
    @Column(name = "guid")
    private UUID guid;


    /**
     * Логин пользователя
     */
    @NotNull
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * Электронный адрес пользователя
     */
    @Column(name = "email")
    private String email;


    /**
     * Фамилия
     */
    @Column(name = "surname")
    private String surname;

    /**
     * Имя
     */
    @Column(name = "name")
    private String name;

    /**
     * Отчество
     */
    @Column(name = "patronymic")
    private String patronymic;

    /**
     * Пароль пользователя
     */
    @Column(name = "password")
    private String passwordHash;

    /**
     * Активен ли пользователь
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Роли пользователя
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(schema = "sec", name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private List<RoleEntity> roleList;

    @OneToMany(mappedBy = "user")
    private List<EmployeeBankEntity> employeeBankList;

    public User extractModel() {
        User model = new User();
        model.setId(this.id);
        model.setGuid(this.guid == null ? null : this.guid.toString());
        model.setUsername(this.username);
        model.setName(this.name);
        model.setSurname(this.surname);
        model.setPatronymic(this.patronymic);
        model.setIsActive(this.isActive);
        model.setEmail(this.email);
        StringBuilder fioBuilder = new StringBuilder();
        StringBuilder shortFioBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(this.surname)) {
            fioBuilder.append(this.surname).append(" ");
            shortFioBuilder.append(this.surname).append(" ");
        }
        if (StringUtils.isNotBlank(this.name)) {
            fioBuilder.append(this.name).append(" ");
            shortFioBuilder.append(this.name, 0, 1).append(". ");
        }
        if (StringUtils.isNotBlank(this.patronymic)) {
            fioBuilder.append(this.patronymic);
            shortFioBuilder.append(this.patronymic, 0, 1).append(".");
        }
        model.setFio(fioBuilder.toString());
        model.setShortFio(shortFioBuilder.toString());
        return model;
    }
}

