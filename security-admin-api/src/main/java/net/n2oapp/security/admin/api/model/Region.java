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

/**
 * Регион
 */
@Getter
@Setter
@ApiModel("Регион")
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Region {

    @JsonProperty
    @ApiModelProperty(value = "Идентификатор")
    private Integer id;

    @JsonProperty
    @ApiModelProperty(value = "Наименование региона")
    private String name;

    @JsonProperty
    @ApiModelProperty(value = "Двухзначный код региона (субъекта) РФ")
    private String code;

    @JsonProperty
    @ApiModelProperty(value = "Код ОКАТО региона (субъекта РФ)")
    private String okato;

    public Region(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
