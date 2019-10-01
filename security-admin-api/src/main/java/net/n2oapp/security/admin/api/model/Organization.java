package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Организация
 */
@Data
@NoArgsConstructor
@ApiModel("Организация")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Organization {

    @JsonProperty
    @ApiModelProperty(value = "Идентификатор")
    private Integer id;

    @JsonProperty
    @ApiModelProperty(value = "Код организации")
    private String code;

    @JsonProperty
    @ApiModelProperty(value = "Краткое наименование организации")
    private String shortName;

    @JsonProperty
    @ApiModelProperty(value = "Код ОГРН (уникальный код организации)")
    private String ogrn;

    @JsonProperty
    @ApiModelProperty(value = "Код ОКПО (используется в стат.формах)")
    private String okpo;

    @JsonProperty
    @ApiModelProperty(value = "Полное наименование организации")
    private String fullName;

}
