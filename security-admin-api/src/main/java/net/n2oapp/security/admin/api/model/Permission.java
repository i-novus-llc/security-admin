package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Permission {

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
    @ApiModelProperty(value = "Идентификатор родителя")
    private Integer parentId;

    @JsonProperty
    @ApiModelProperty(value = "Имеет ли детей")
    private Boolean hasChildren;
}
