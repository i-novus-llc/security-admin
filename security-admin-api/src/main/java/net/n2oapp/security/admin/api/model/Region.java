package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Регион
 */
@Data
@NoArgsConstructor
@ApiModel("Регион")
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

}
