/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Клиент(Приложение)
 */

@Getter
@Setter
@NoArgsConstructor
@ApiModel("Клиент")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Client {

    @JsonProperty
    @ApiModelProperty(value = "Имя клиента", required = true)
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
    @ApiModelProperty("Разрешённые для редиректа URI")
    private String redirectUris;

    @JsonProperty
    @ApiModelProperty("Время жизни токена доступа (в минутах)")
    private Integer accessTokenValidityMinutes;

    @JsonProperty
    @ApiModelProperty("Время жизни Refresh-токена (в минутах)")
    private Integer refreshTokenValidityMinutes;

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

    @JsonProperty
    private String systemCode;
}
