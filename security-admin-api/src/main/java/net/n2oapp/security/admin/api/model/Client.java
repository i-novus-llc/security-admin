package net.n2oapp.security.admin.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ApiModelProperty("URI разрешённые для редиректа")
    private Set<String> redirectUris;

    @ApiModelProperty("Время жизни токена")
    private Integer accessTokenLifetime;

    @ApiModelProperty("Время жизни токена для обновления токенов")
    private Integer refreshTokenLifetime;

    @ApiModelProperty("URL для выходы")
    private String logoutUrl;

}
