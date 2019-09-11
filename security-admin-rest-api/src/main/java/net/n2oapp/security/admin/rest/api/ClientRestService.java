package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
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
@Api("Клиенты")
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
    @Path("/getOrCreate/{clientId}")
    Client getOrCreate(@PathParam("clientId") String clientId);
}
