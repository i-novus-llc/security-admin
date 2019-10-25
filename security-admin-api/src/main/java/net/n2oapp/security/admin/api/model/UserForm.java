package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Модель пользователя для actions
 */
@Data
@ApiModel("Пользователь")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserForm {

    @ApiModelProperty(value = "Идентификатор")
    private Integer id;

    @ApiModelProperty(value = "Наименование sso")
    private String extSys;

    @ApiModelProperty(value = "Внешний идентификатор")
    private String extUid;

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
    private String password;

    @ApiModelProperty(value = "Пароль")
    private String passwordCheck;

    @ApiModelProperty(value = "Временный пароль")
    private String temporaryPassword;

    @ApiModelProperty(value = "Идет ли отправка пароля на почту")
    private Boolean sendOnEmail;

    @ApiModelProperty(value = "Активен ли пользователь")
    private Boolean isActive;

    @ApiModelProperty(value = "Список идентификаторов ролей")
    private List<Integer> roles;

    @ApiModelProperty(value = "СНИЛС пользователся")
    private String snils;

    @ApiModelProperty(value = "Уровень пользователя")
    private String userLevel;

    @ApiModelProperty(value = "Код департамента")
    private Integer departmentId;

    @ApiModelProperty(value = "Код региона")
    private Integer regionId;

    @ApiModelProperty(value = "Код организации")
    private Integer organizationId;
}
