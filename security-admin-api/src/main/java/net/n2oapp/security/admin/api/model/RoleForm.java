package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class RoleForm {

    @ApiModelProperty(value = "Идентификатор")
    private Integer id;

    @ApiModelProperty(value = "Название")
    private String name;

    @ApiModelProperty(value = "Код")
    private String code;

    @ApiModelProperty(value = "Описание")
    private String description;

    @ApiModelProperty(value = "Права доступа")
    private List<String> permissions;

    @ApiModelProperty(value = "Код системы")
    private String systemCode;

    @ApiModelProperty(value = "Уровень пользователя")
    private String userLevel;

}