package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Система
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = "applications")
@NoArgsConstructor
@ApiModel("Система")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppSystem extends AppSystemForm {

    @JsonProperty
    @ApiModelProperty(value = "Приложения")
    private List<Application> applications;

    public AppSystem(String code) {
        super(code);
    }
}