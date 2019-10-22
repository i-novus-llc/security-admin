package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Департамент
 */
@Data
@NoArgsConstructor
@ApiModel("Департамент")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Department {

    @JsonProperty
    @ApiModelProperty(value = "Идентификатор")
    private Integer id;

    @JsonProperty
    @ApiModelProperty(value = "Название департамента МПС")
    private String name;

    @JsonProperty
    @ApiModelProperty(value = "Код департамента МПС")
    private String code;

}
