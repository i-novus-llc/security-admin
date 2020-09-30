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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Пользователь
 */
@Data
@NoArgsConstructor
@ApiModel("Форма регистрации пользователя")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegisterForm {

    @ApiModelProperty(value = "Email пользователя")
    private String email;

    @ApiModelProperty(value = "Имя пользователя")
    private String username;

    @ApiModelProperty(value = "Пароль")
    private String password;

    @ApiModelProperty(value = "Тип аккаунта")
    private String accountTypeCode;

    @ApiModelProperty(value = "Имя")
    private String name;

    @ApiModelProperty(value = "Фамилия")
    private String surname;

    @ApiModelProperty(value = "Отчество")
    private String patronymic;

    @ApiModelProperty(value = "Отправлять письмо с паролем на е-мейл")
    private Boolean sendPasswordToEmail;

    @ApiModelProperty(value = "Активен ли пользователь")
    private Boolean isActive;

}
