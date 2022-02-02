package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Модель аккаунта пользователя для показа на UI
 */
@Getter
@Setter
@ApiModel("Аккаунт пользователя")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {
    @ApiModelProperty(value = "Идентификатор аккаунта")
    private Integer id;

    @ApiModelProperty(value = "Идентификатор пользователя связанного с аккаунтом")
    private Integer userId;

    @ApiModelProperty(value = "Наименование аккаунта")
    private String name;

    @ApiModelProperty(value = "Активен ли аккаунт")
    private Boolean isActive;

    @ApiModelProperty(value = "Список ролей")
    private List<Role> roles;

    @ApiModelProperty(value = "Уровень аккаунта, для которого предназначена роль")
    private UserLevel userLevel;

    @ApiModelProperty(value = "Департамент")
    private Department department;

    @ApiModelProperty(value = "Регион")
    private Region region;

    @ApiModelProperty(value = "Организация")
    private Organization organization;

    private String extSys;

    private String extUid;
}
