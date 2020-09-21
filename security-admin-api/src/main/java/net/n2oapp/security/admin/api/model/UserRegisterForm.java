package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Пользователь
 */
@Data
@NoArgsConstructor
@ApiModel("Форма регистрации пользователя")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegisterForm {

    @ApiModelProperty(value = "Email пользователя")
    private String email;

    @ApiModelProperty(value = "Имя пользователя")
    private String username;

    @ApiModelProperty(value = "Пароль")
    private String password;

    @ApiModelProperty(value = "Тип аккаунта")
    private String accountTypeCode;

    @ApiModelProperty(value = "Имя")
    private String name;

    @ApiModelProperty(value = "Фамилия")
    private String surname;

    @ApiModelProperty(value = "Отчество")
    private String patronymic;

    @ApiModelProperty(value = "Отправлять письмо с паролем на е-мейл")
    private Boolean sendPasswordToEmail;

    @ApiModelProperty(value = "Активен ли пользователь")
    private Boolean isActive;

}
