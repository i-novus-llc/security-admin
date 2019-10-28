package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Модель пользователя для показа на UI
 */
@Data
@ApiModel("Пользователь")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @ApiModelProperty(value = "Идентификатор")
    private Integer id;

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

    @ApiModelProperty(value = "Временный пароль")
    private String temporaryPassword;

    @ApiModelProperty(value = "Активен ли пользователь")
    private Boolean isActive;

    @ApiModelProperty(value = "Список ролей")
    private List<Role> roles;

    @ApiModelProperty(value = "СНИЛС пользователя")
    private String snils;

    @ApiModelProperty(value = "Уровень пользователя, для которого предназначена роль")
    private UserLevel userLevel;

    @ApiModelProperty(value = "Департамент")
    private Department department;

    @ApiModelProperty(value = "Регион")
    private Region region;

    @ApiModelProperty(value = "Организация")
    private Organization organization;
}
