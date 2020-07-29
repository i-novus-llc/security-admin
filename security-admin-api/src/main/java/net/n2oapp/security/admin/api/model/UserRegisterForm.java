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
@ApiModel("Клиент")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegisterForm {

    @ApiModelProperty(value = "Email пользователя")
    private String email;

    @ApiModelProperty(value = "Имя пользователя")
    private String username;

    @ApiModelProperty(value = "Пароль")
    private String password;

    @ApiModelProperty(value = "Повторный ввод пароля")
    private String passwordCheck;

    @ApiModelProperty(value = "Тип аккаунта")
    private String accountTypeCode;

}
