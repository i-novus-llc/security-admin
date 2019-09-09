package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Клиент(Приложение)
 */

@Data
@NoArgsConstructor
@ApiModel("Клиент")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Client {

    @JsonProperty
    @ApiModelProperty("Имя клиента")
    private String clientId;

    @JsonProperty
    @ApiModelProperty("Секрет клиента")
    private String clientSecret;

    @JsonProperty
    @ApiModelProperty("Вход от системы")
    private Boolean isClientGrant;

    @JsonProperty
    @ApiModelProperty("Вход по логину")
    private Boolean isResourceOwnerPass;

    @JsonProperty
    @ApiModelProperty("Вход через браузер")
    private Boolean isAuthorizationCode;

    @JsonProperty
    @ApiModelProperty("URI разрешённые для редиректа")
    private String redirectUris;

    @JsonProperty
    @ApiModelProperty("Время жизни токена")
    private Integer accessTokenLifetime;

    @JsonProperty
    @ApiModelProperty("Время жизни токена для обновления токенов")
    private Integer refreshTokenLifetime;

    @JsonProperty
    @ApiModelProperty("Ссылка для выхода")
    private String logoutUrl;

    @JsonProperty
    @ApiModelProperty(value = "Список ролей")
    private List<Role> roles;

    @JsonProperty
    @ApiModelProperty(value = "Список идентификаторов ролей")
    private List<Integer> rolesIds;

    @JsonProperty
    private Boolean enabled;
}
