package net.n2oapp.security.admin.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Система
 */
@Data
@NoArgsConstructor
@ApiModel("Система")
public class AppSystemForm {

    @ApiModelProperty(value = "Код")
    private String code;

    @ApiModelProperty(value = "Название")
    private String name;

    @ApiModelProperty(value = "Описание")
    private String description;

    @ApiModelProperty(value = "Приложения")
    private List<String> services;


}