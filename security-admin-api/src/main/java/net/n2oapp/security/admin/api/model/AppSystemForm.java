package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Система
 */
@Data
@NoArgsConstructor
@ApiModel("Система")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppSystemForm {

    @ApiModelProperty(value = "Код")
    private String code;

    @ApiModelProperty(value = "Название")
    private String name;

    @ApiModelProperty(value = "Описание")
    private String description;

}