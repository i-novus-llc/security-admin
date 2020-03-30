package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Категория организации
 */
@Data
@NoArgsConstructor
@ApiModel("Категория организации")
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrgCategory {

    @JsonProperty
    @ApiModelProperty(value = "Уникальный идентификатор записи")
    private Integer id;

    @JsonProperty
    @ApiModelProperty(value = "Код категории организации")
    private String code;

    @JsonProperty
    @ApiModelProperty(value = "Наименование категории организации")
    private String name;

    @JsonProperty
    @ApiModelProperty(value = "Краткое описание или расшифровка категории")
    private String description;

}
