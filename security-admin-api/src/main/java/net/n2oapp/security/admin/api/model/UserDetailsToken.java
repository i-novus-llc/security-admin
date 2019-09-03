package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Модель данных пользователя для интеграции с sso сервером
 */
@Data
@ApiModel("Пользователь")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetailsToken {

    @ApiModelProperty(value = "Наименование sso")
    private String extSys;

    @ApiModelProperty(value = "Идентификатор sso")
    private String extUid;

    @ApiModelProperty(value = "Имя пользователя")
    private String username;

    @ApiModelProperty(value = "Имя")
    private String name;

    @ApiModelProperty(value = "Фамилия")
    private String surname;

    @ApiModelProperty(value = "Электронный адрес")
    private String email;

    @ApiModelProperty(value = "Список названий ролей")
    private List<String> roleNames;
}
