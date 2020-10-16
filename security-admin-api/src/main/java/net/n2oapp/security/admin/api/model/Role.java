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

import java.util.List;

/**
 * Роль
 */
@Getter
@Setter
@ApiModel("Роль")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role {

    @JsonProperty
    @ApiModelProperty(value = "Идентификатор")
    private Integer id;

    @JsonProperty
    @ApiModelProperty(value = "Название")
    private String name;

    @JsonProperty
    @ApiModelProperty(value = "Код")
    private String code;

    @JsonProperty
    @ApiModelProperty(value = "Описание")
    private String description;

    @JsonProperty
    @ApiModelProperty(value = "Cистема")
    private AppSystem system;

    @JsonProperty
    @ApiModelProperty(value = "Права доступа")
    private List<Permission> permissions;

    @JsonProperty
    @ApiModelProperty(value = "Имя с системой")
    private String nameWithSystem;

    @JsonProperty
    @ApiModelProperty(value = "Уровень пользователя")
    private UserLevel userLevel;
}