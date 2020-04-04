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
import net.n2oapp.security.admin.rest.api.criteria.RestUserDetailsToken;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис для получения информации о пользователе
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Информация о пользователе", authorizations = @Authorization(value = "oauth2"))
public interface UserDetailsRestService {

    @POST
    @Path("/details")
    @ApiOperation("Загрузить информацию о пользователе, по его имени и списку ролей")
    @ApiResponse(code = 200, message = "Страница пользователей")
    User loadDetails(@ApiParam(value = "Информация о пользователе") RestUserDetailsToken token);
}
