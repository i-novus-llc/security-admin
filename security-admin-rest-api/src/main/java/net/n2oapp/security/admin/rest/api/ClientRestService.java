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
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.rest.api.criteria.RestClientCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления клиентами
 */
@Path("/clients")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Клиенты", authorizations = @Authorization(value = "oauth2"))
public interface ClientRestService {

    @GET
    @Path("/")
    @ApiOperation("Все клиенты")
    @ApiResponse(code = 200, message = "Найти клиентов по критериям поиска")
    Page<Client> findAll(@BeanParam RestClientCriteria criteria);

    @GET
    @Path("/{clientId}")
    @ApiOperation("Получить клиента по идентификатору")
    @ApiResponse(code = 200, message = "Клиенты")
    Client getByClientId(@ApiParam(value = "Имя клиента") @PathParam("clientId") String clientId);

    @POST
    @Path("/")
    @ApiOperation("Создать клиента")
    @ApiResponse(code = 200, message = "Созданный клиент")
    Client create(@ApiParam(value = "Клиент") Client clientForm);

    @PUT
    @Path("/")
    @ApiOperation("Изменить клиента")
    @ApiResponse(code = 200, message = "Измененный клиент")
    Client update(@ApiParam(value = "Клиент") Client clientForm);

    @DELETE
    @Path("/{clientId}")
    @ApiOperation("Удалить клиента")
    @ApiResponse(code = 204, message = "Клиент удален")
    void delete(@ApiParam(value = "Имя клиента") @PathParam("clientId") String clientId);

    @POST
    @Path("/persist")
    Client persist(Client clientForm);

    @GET
    @Path("/getDefaultClient/{clientId}")
    Client getDefaultClient(@PathParam("clientId") String clientId);
}
