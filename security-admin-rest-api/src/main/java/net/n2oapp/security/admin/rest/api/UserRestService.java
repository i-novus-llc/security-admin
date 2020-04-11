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
package net.n2oapp.security.admin.rest.api;


import io.swagger.annotations.*;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления пользоватлями
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Пользователи", authorizations = @Authorization(value = "oauth2"))
public interface UserRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти всех пользователей")
    @ApiResponse(code = 200, message = "Страница пользователей")
    Page<User> findAll(@BeanParam RestUserCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получить пользователя по идентификатору")
    @ApiResponse(code = 200, message = "Пользователь")
    User getById(@ApiParam(value = "Идентификатор") @PathParam("id") Integer id);


    @POST
    @Path("/")
    @ApiOperation("Создать пользователя")
    @ApiResponse(code = 200, message = "Созданный пользователь")
    User create(@ApiParam(value = "Пользователь") UserForm user);

    @PUT
    @Path("/")
    @ApiOperation("Изменить пользователя")
    @ApiResponse(code = 200, message = "Измененный пользователь")
    User update(@ApiParam(value = "Пользователь") UserForm user);

    @DELETE
    @Path("/{id}")
    @ApiOperation("Удалить пользователя")
    @ApiResponse(code = 200, message = "Пользователь удален")
    void delete(@ApiParam(value = "Идентификатор") @PathParam("id") Integer id);

    @PUT
    @Path("/changeActive/{id}")
    @ApiOperation("Изменить статус пользователя")
    @ApiResponse(code = 200, message = "Пользователь с измененным статусом")
    User changeActive(@ApiParam(value = "Идентификатор") @PathParam("id") Integer id);

    @GET
    @Path("/simpleDetails")
    @ApiOperation("Загрузить простейшую информацию о пользователе (имя, почта и временный пароль)")
    @ApiResponse(code = 200, message = "Временный пароль")
    User loadSimpleDetails(@ApiParam(value = "Идентификатор") @QueryParam("id") Integer id);

    @PUT
    @Path("/resetPassword")
    @ApiOperation("Сбросить пароль")
    @ApiResponse(code = 200, message = "Пароль сброшен")
    void resetPassword(@ApiParam(value = "Пользователь") UserForm user);
}
