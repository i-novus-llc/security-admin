/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Модель пользователя для actions
 */
@Getter
@Setter
@ApiModel("Пользователь")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserForm {

    @ApiModelProperty(value = "Идентификатор")
    private Integer id;

    @ApiModelProperty(value = "Наименование sso")
    private String extSys;

    @ApiModelProperty(value = "Внешний идентификатор")
    private String extUid;

    @ApiModelProperty(value = "Имя пользователя", required = true)
    private String username;

    @ApiModelProperty(value = "Электронный адрес")
    private String email;

    @ApiModelProperty(value = "Фамилия")
    private String surname;

    @ApiModelProperty(value = "Имя")
    private String name;

    @ApiModelProperty(value = "Отчество")
    private String patronymic;

    @ApiModelProperty(value = "Пароль")
    private String password;

    @ApiModelProperty(value = "Пароль")
    private String passwordCheck;

    @ApiModelProperty(value = "Временный пароль")
    private String temporaryPassword;

    @ApiModelProperty(value = "Идет ли отправка пароля на почту")
    private Boolean sendOnEmail;

    @ApiModelProperty(value = "Активен ли пользователь")
    private Boolean isActive;

    @ApiModelProperty(value = "Список идентификаторов ролей")
    private List<Integer> roles;

    @ApiModelProperty(value = "СНИЛС пользователя")
    private String snils;

    @ApiModelProperty(value = "Уровень пользователя")
    private String userLevel;

    @ApiModelProperty(value = "Код департамента")
    private Integer departmentId;

    @ApiModelProperty(value = "Код региона")
    private Integer regionId;

    @ApiModelProperty(value = "Код организации")
    private Integer organizationId;

    @ApiModelProperty(value = "Статус регистрации пользователя")
    private UserStatus status;

    @ApiModelProperty(value = "Тип аккаунта")
    private String accountTypeCode;

    @ApiParam("Срок действия учётной записи пользователя")
    private LocalDateTime expirationDate;
}
