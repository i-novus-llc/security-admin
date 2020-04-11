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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
import net.n2oapp.security.admin.api.model.UserLevel;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления уровнями пользователя
 */
@Path("/userLevels")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Уровни пользователя", authorizations = @Authorization(value = "oauth2"))
public interface UserLevelRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти все уровни пользователя")
    @ApiResponse(code = 200, message = "Страница уровней пользователя")
    Page<UserLevel> getAll();

    @GET
    @Path("/forFilter")
    @ApiOperation("Найти все уровни пользователя для фильтра")
    @ApiResponse(code = 200, message = "Страница уровней пользователя")
    Page<UserLevel> getAllForFilter(@QueryParam("name") String name);
}
