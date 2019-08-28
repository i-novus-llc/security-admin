package net.n2oapp.security.admin.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Модель пользователя для показа на UI
 */
@Data
@ApiModel("Пользователь")
public class User {

    @ApiModelProperty(value = "Идентификатор")
    private Integer id;

    @ApiModelProperty(value = "Наименование sso")
    private String extSys;

    @ApiModelProperty(value = "Внешний идентификатор")
    private String guid;

    @ApiModelProperty(value = "Имя пользователя")
    private String username;

    @ApiModelProperty(value = "ФИО")
    private String fio;

    @ApiModelProperty(value = "Электронный адрес")
    private String email;

    @ApiModelProperty(value = "Фамилия")
    private String surname;

    @ApiModelProperty(value = "Имя")
    private String name;

    @ApiModelProperty(value = "Отчество")
    private String patronymic;

    @ApiModelProperty(value = "Пароль")
    private String password;

    @ApiModelProperty(value = "Пароль")
    private String passwordHash;

    @ApiModelProperty(value = "Пароль")
    private String passwordCheck;

    @ApiModelProperty(value = "Активен ли пользователь")
    private Boolean isActive;

    @ApiModelProperty(value = "Список ролей")
    private List<Role> roles;
}
