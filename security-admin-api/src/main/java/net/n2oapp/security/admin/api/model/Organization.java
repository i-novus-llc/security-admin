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
 * Организация
 */
@Getter
@Setter
@ApiModel("Организация")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Organization {

    @JsonProperty
    @ApiModelProperty(value = "Идентификатор")
    private Integer id;

    @JsonProperty
    @ApiModelProperty(value = "Код организации")
    private String code;

    @JsonProperty
    @ApiModelProperty(value = "Краткое наименование организации", required = true)
    private String shortName;

    @JsonProperty
    @ApiModelProperty(value = "Код ОГРН (уникальный код организации)", required = true)
    private String ogrn;

    @JsonProperty
    @ApiModelProperty(value = "Код ОКПО (используется в стат.формах)")
    private String okpo;

    @JsonProperty
    @ApiModelProperty(value = "Полное наименование организации")
    private String fullName;

    @JsonProperty
    @ApiModelProperty(value = "ИНН организации")
    private String inn;

    @JsonProperty
    @ApiModelProperty(value = "КПП организации")
    private String kpp;

    @JsonProperty
    @ApiModelProperty(value = "Юридический адрес")
    private String legalAddress;

    @JsonProperty
    @ApiModelProperty(value = "Электронная почта")
    private String email;

    @JsonProperty
    @ApiModelProperty(value = "Идентификаторы ролей")
    private List<Integer> roleIds;

    @JsonProperty
    @ApiModelProperty(value = "Роли")
    private List<Role> roles;

    @JsonProperty
    @ApiModelProperty(value = "Идентификатор во внешней системе")
    private String extUid;

    @JsonProperty
    @ApiModelProperty(value = "Идентификаторы категорий")
    private List<Integer> orgCategoryIds;

    @JsonProperty
    @ApiModelProperty(value = "Категории организаций")
    private List<OrgCategory> orgCategories;
}
