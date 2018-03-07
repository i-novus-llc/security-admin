package net.n2oapp.security.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Role;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления ролями  пользователей
 */

@Path("/roles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("REST сервис регистрации ролей  пользователей")
public interface RoleRestService {
    @GET
    @Path("/")
    @ApiOperation("Фильтрация ролей")
    @ApiResponse(code = 200, message = "Роли")
    Page<Role> search(@QueryParam("page")@DefaultValue("1") Integer page, @QueryParam("size") @DefaultValue("10") Integer size,
                      @QueryParam("name") String name,@QueryParam("description") String description);
    @GET
    @Path("/{id}")
    @ApiOperation("Получение ролей")
    @ApiResponse(code = 200, message = "Роли")
    Role getById(@PathParam("id") Integer id);


    @POST
    @Path("/create")
    @ApiOperation("Создание ролей")
    @ApiResponse(code = 200, message = "Роли")
    Role create(Role role);

    @PUT
    @Path("/update")
    @ApiOperation("Изменение ролей")
    @ApiResponse(code = 200, message = "Роли")
    Role update(Role role);

    @DELETE
    @Path("/delete/{id}")
    @ApiOperation("Удаление ролей")
    @ApiResponse(code = 200, message = "Роли")
    void  delete(@PathParam("id") Integer id);

}


