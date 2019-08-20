package net.n2oapp.security.admin.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Приложение
 */
@Data
@NoArgsConstructor
@ApiModel("Служба")
public class ApplicationForm {

    @ApiModelProperty("Код")
    private String code;

    @ApiModelProperty("Наименование")
    private String name;

    @ApiModelProperty("Код системы")
    private String systemCode;
}