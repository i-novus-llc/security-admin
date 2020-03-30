package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Форма для обновления права доступа
 */
@Data
@NoArgsConstructor
@ApiModel("Право доступа")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionUpdateForm {

    @JsonProperty
    @ApiModelProperty(value = "Название")
    private String name;

    @JsonProperty
    @ApiModelProperty(value = "Код")
    private String code;

    @JsonProperty
    @ApiModelProperty(value = "Код родителя")
    private Permission parent;

    @JsonProperty
    @ApiModelProperty(value = "Уровень пользователя")
    private UserLevel userLevel;

}