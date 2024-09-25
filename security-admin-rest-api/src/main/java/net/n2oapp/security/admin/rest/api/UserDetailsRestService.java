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
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.rest.api.criteria.RestUserDetailsToken;

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
    @ApiResponses({
            @ApiResponse(code = 200, message = "Информация о пользователе"),
            @ApiResponse(code = 400, message = "Неккоректный запрос. Отсутвуют обязательные поля или заполнены не корректными данными")
    })
    User loadDetails(@ApiParam(value = "Информация о пользователе") RestUserDetailsToken token);
}
