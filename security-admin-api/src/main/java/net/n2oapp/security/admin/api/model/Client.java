package net.n2oapp.security.admin.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Клиент(Приложение)
 */

@Data
@NoArgsConstructor
@ApiModel("Клиент")
public class Client {

    @ApiModelProperty("Имя клиента")
    private String clientId;

    @ApiModelProperty("Секрет клиента")
    private String clientSecret;

    @ApiModelProperty("Тип авторизации")
    private Set<String> authorizedGrantTypes;

    @ApiModelProperty("URI разрешённые для редиректа")
    private Set<String> registeredRedirectUri;

    @ApiModelProperty("Время жизни токена")
    private Integer accessTokenValiditySeconds;

    @ApiModelProperty("Время жизни токена для обновления токенов")
    private Integer refreshTokenValiditySeconds;

    @ApiModelProperty("URL для выходы")
    private String logoutUrl;

}
