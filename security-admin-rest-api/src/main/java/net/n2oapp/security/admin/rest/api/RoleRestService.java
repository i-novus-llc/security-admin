package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.*;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.rest.api.criteria.RestRoleCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления ролями  пользователей
 */
@Path("/roles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Роли", authorizations = @Authorization(value = "oauth2"))
public interface RoleRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти роли по критериям поиска")
    @ApiResponse(code = 200, message = "Страница ролей")
    Page<Role> findAll(@BeanParam RestRoleCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получить роль по идентификатору")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Роль"),
            @ApiResponse(code = 404, message = "Роль не найдена")
    })
    Role getById(@ApiParam(value = "Идентификатор") @PathParam("id") Integer id);

    @POST
    @Path("/")
    @ApiOperation("Создать роль")
    @ApiResponse(code = 200, message = "Созданная роль")
    Role create(@ApiParam(value = "Роль") RoleForm role);

    @PUT
    @Path("/")
    @ApiOperation("Изменить роль")
    @ApiResponse(code = 200, message = "Измененная роль")
    Role update(@ApiParam(value = "Роль") RoleForm role);

    @DELETE
    @Path("/{id}")
    @ApiOperation("Удалить роль")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Роль удалена"),
            @ApiResponse(code = 404, message = "Роль не найдена")
    })
    void delete(@ApiParam(value = "Идентификатор") @PathParam("id") Integer id);
}


