package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.n2oapp.security.admin.api.model.Organization;

/**
 * REST сервис для создание, изменения и удаления организаций
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Организации", authorizations = @Authorization(value = "oauth2"))
public interface OrganizationPersistRestService {

    @POST
    @Path("/organizations")
    @ApiOperation("Создать организацию")
    @ApiResponse(code = 200, message = "Организация создана")
    Organization create(@ApiParam("Организация") Organization organization);

    @PUT
    @Path("/organizations")
    @ApiOperation("Обновить организацию")
    @ApiResponse(code = 200, message = "Организация обновлена")
    Organization update(@ApiParam("Организация") Organization organization);

    @DELETE
    @Path("/organizations/{id}")
    @ApiOperation("Удалить организацию")
    @ApiResponse(code = 200, message = "Организация удалена")
    void delete(@ApiParam(value = "Уникальный идентификатор записи организации") @PathParam("id") Integer id);
}
