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

import java.io.Serializable;

/**
 * Система
 */
@Getter
@Setter
@NoArgsConstructor
@ApiModel("Система")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppSystemForm implements Serializable {

    @JsonProperty
    @ApiModelProperty(value = "Код", required = true)
    private String code;

    @JsonProperty
    @ApiModelProperty(value = "Название", required = true)
    private String name;

    @JsonProperty
    @ApiModelProperty(value = "Описание")
    private String description;

    @JsonProperty
    @ApiModelProperty(value = "Краткое наименование")
    private String shortName;

    @JsonProperty
    @ApiModelProperty(value = "Иконка")
    private String icon;

    @JsonProperty
    @ApiModelProperty(value = "Адрес")
    private String url;

    @JsonProperty
    @ApiModelProperty(value = "Признак работы в режиме без авторизации")
    private Boolean publicAccess;

    @JsonProperty
    @ApiModelProperty(value = "Порядок отображения подсистемы")
    private Integer viewOrder;

    @JsonProperty
    @ApiModelProperty(value = "Отображение системы в едином интерфейсе")
    private Boolean showOnInterface;

    public AppSystemForm(String code) {
        this.code = code;
    }
}