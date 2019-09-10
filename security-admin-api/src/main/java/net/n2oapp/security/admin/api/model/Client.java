package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * Клиент(Приложение)
 */

@Data
@NoArgsConstructor
@ApiModel("Клиент")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Client {

    @ApiModelProperty("Имя клиента")
    private String clientId;

    @ApiModelProperty("Секрет клиента")
    private String clientSecret;

    @ApiModelProperty("Тип авторизации")
    private Set<String> grantTypes;

    @ApiModelProperty("Разрешённые для редиректа URI")
    private Set<String> redirectUris;

    @ApiModelProperty("Время жизни токена доступа")
    private Integer accessTokenLifetime;

    @ApiModelProperty("Время жизни токена обновления")
    private Integer refreshTokenLifetime;

    @ApiModelProperty("URL для выхода")
    private String logoutUrl;

    @ApiModelProperty("Роли")
    private List<Role> roles;

}
