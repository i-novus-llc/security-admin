package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Роль
 */
@Data
@NoArgsConstructor
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
    @ApiModelProperty(value = "Права доступа")
    private List<Permission> permissions;
}