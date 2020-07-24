package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Тип аккаунта
 */
@Data
@NoArgsConstructor
@ApiModel("Тип аккаунта")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountType {
    @JsonProperty
    @ApiModelProperty("Идентификатор")
    private Integer id;

    @JsonProperty
    @ApiModelProperty("Код")
    private String code;

    @JsonProperty
    @ApiModelProperty("Наименование")
    private String name;

    @JsonProperty
    @ApiModelProperty("Описание")
    private String description;

    @JsonProperty
    @ApiModelProperty("Уровень пользователя")
    private UserLevel userLevel;

    @JsonProperty
    @ApiModelProperty(value = "Список ролей")
    private List<Role> roles;

    @JsonProperty
    @ApiModelProperty("Список идентификаторов ролей")
    private List<Integer> roleIds;

    @JsonProperty
    @ApiModelProperty("Статус")
    private Boolean status;
}
