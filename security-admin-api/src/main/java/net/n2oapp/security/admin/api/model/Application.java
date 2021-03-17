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
import lombok.Setter;

import java.io.Serializable;

/**
 * Приложение
 */
@Getter
@Setter
@ApiModel("Приложение")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application implements Serializable {

    @JsonProperty
    @ApiModelProperty(value = "Код", required = true)
    private String code;

    @JsonProperty
    @ApiModelProperty(value = "Наименование", required = true)
    private String name;

    @JsonProperty
    @ApiModelProperty(value = "Код системы", required = true)
    private String systemCode;

    @JsonProperty
    @ApiModelProperty("Имя системы")
    private String systemName;

    @JsonProperty
    @ApiModelProperty("Протокол OAuth 2.0")
    private Boolean oAuth;


    @JsonProperty
    @ApiModelProperty("Клиент(Приложение)")
    private Client client;

}