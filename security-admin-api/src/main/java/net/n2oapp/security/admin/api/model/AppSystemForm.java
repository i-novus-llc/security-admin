package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty
    @ApiModelProperty(value = "Код")
    private String code;

    @JsonProperty
    @ApiModelProperty(value = "Название")
    private String name;

    @JsonProperty
    @ApiModelProperty(value = "Описание")
    private String description;

    @JsonProperty
    @ApiModelProperty(value = "Краткое наименование")
    private String shortName;

    @JsonProperty
    @ApiModelProperty(value = "Иконка")
    private String icon;

    @JsonProperty
    @ApiModelProperty(value = "Адрес")
    private String url;

    @JsonProperty
    @ApiModelProperty(value = "Признак работы в режиме без авторизации")
    private Boolean publicAccess;

    @JsonProperty
    @ApiModelProperty(value = "Порядок отображения подсистемы")
    private Integer viewOrder;

    public AppSystemForm(String code) {
        this.code = code;
    }
}