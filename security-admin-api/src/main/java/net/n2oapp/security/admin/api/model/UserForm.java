package net.n2oapp.security.admin.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Модель пользователя для actions
 */
@Data
@ApiModel("Пользователь")
public class UserForm {

    @ApiModelProperty(value = "Идентификатор")
    private Integer id;

    @ApiModelProperty(value = "Внешний идентификатор")
    private String guid;

    @ApiModelProperty(value = "Имя пользователя")
    private String username;

    @ApiModelProperty(value = "Электронный адрес")
    private String email;

    @ApiModelProperty(value = "Фамилия")
    private String surname;

    @ApiModelProperty(value = "Имя")
    private String name;

    @ApiModelProperty(value = "Отчество")
    private String patronymic;

    @ApiModelProperty(value = "Пароль")
    private String newPassword;

    @ApiModelProperty(value = "Пароль")
    private String password;

    @ApiModelProperty(value = "Пароль")
    private String passwordCheck;

    @ApiModelProperty(value = "Активен ли пользователь")
    private Boolean isActive;

    @ApiModelProperty(value = "Список идентификаторов ролей")
    private List<Integer> roles;
}
