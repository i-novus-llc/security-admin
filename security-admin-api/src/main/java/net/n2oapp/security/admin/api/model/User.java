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
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Модель пользователя для показа на UI
 */
@Getter
@Setter
@ApiModel("Пользователь")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @ApiModelProperty(value = "Идентификатор")
    private Integer id;

    @ApiModelProperty(value = "Имя пользователя")
    private String username;

    @ApiModelProperty(value = "ФИО")
    private String fio;

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
    private String passwordHash;

    @ApiModelProperty(value = "Пароль")
    private String passwordCheck;

    @ApiModelProperty(value = "Временный пароль")
    private String temporaryPassword;

    @ApiModelProperty(value = "Активен ли пользователь")
    private Boolean isActive;

    @ApiModelProperty(value = "Список ролей")
    private List<Role> roles;

    @ApiModelProperty(value = "СНИЛС пользователя")
    private String snils;

    @ApiModelProperty(value = "Уровень пользователя, для которого предназначена роль")
    private UserLevel userLevel;

    @ApiModelProperty(value = "Департамент")
    private Department department;

    @ApiModelProperty(value = "Регион")
    private Region region;

    @ApiModelProperty(value = "Организация")
    private Organization organization;

    @ApiModelProperty(value = "Статус пользователя")
    private UserStatus status;

    @ApiModelProperty(value = "Идентификатор клиента в системе уведомлений")
    private Integer clientId;

    @ApiModelProperty(value = "Действующий аккаунт")
    private Boolean isAccountNonExpired;
}
