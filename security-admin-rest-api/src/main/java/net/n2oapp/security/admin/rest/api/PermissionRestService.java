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
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.PermissionUpdateForm;
import net.n2oapp.security.admin.rest.api.criteria.RestPermissionCriteria;
import org.springframework.data.domain.Page;

/**
 * REST сервис управления правами доступа
 */
@Path("/permissions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Права доступа", authorizations = @Authorization(value = "oauth2"))
public interface PermissionRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти все права доступа")
    @ApiResponse(code = 200, message = "Страница прав доступа")
    Page<Permission> getAll(@ApiParam(value = "Код родителя") @QueryParam("parentCode") String parentCode,
                            @ApiParam(value = "Критерия поиска") @BeanParam RestPermissionCriteria criteria);


    @GET
    @Path("/{code}")
    @ApiOperation("Получить право доступа по идентификатору")
    @ApiResponse(code = 200, message = "Права доступа")
    Permission getById(@ApiParam(value = "Код") @PathParam("code") String code);

    @POST
    @Path("/")
    @ApiOperation("Создать право доступа")
    @ApiResponse(code = 200, message = "Созданное право доступа")
    Permission create(@ApiParam(value = "Право доступа") Permission permission);

    @PUT
    @Path("/")
    @ApiOperation("Изменить право доступа")
    @ApiResponse(code = 200, message = "Измененное право доступа")
    Permission update(@ApiParam(value = "Право доступа") PermissionUpdateForm permission);

    @DELETE
    @Path("/{code}")
    @ApiOperation("Удалить право доступа")
    @ApiResponse(code = 200, message = "Право доступа удалено")
    void delete(@ApiParam(value = "Код") @PathParam("code") String code);
}
