package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Приложение
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "systemName")
@ApiModel("Приложение")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application {

    @JsonProperty
    @ApiModelProperty("Код")
    private String code;

    @JsonProperty
    @ApiModelProperty("Наименование")
    private String name;

    @JsonProperty
    @ApiModelProperty("Код системы")
    private String systemCode;

    @JsonProperty
    @ApiModelProperty("Имя системы")
    private String systemName;

    @JsonProperty
    @ApiModelProperty("Протокол OAuth 2.0")
    private Boolean oAuth;
}