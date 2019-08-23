package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Право доступа
 */
@Data
@NoArgsConstructor
@ApiModel("Право доступа")
public class Permission {

    @JsonProperty
    @ApiModelProperty(value = "Название")
    private String name;

    @JsonProperty
    @ApiModelProperty(value = "Код")
    private String code;

    @JsonProperty
    @ApiModelProperty(value = "Код родителя")
    private String parentCode;

    @JsonProperty
    @ApiModelProperty(value = "Имеет ли детей")
    private Boolean hasChildren;
}
