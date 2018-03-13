package net.n2oapp.security.rest.api;

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
    @ApiOperation("Найти роли по критерия м поиска")
    @ApiResponse(code = 200, message = "Страница ролей")
    Page<Role> search(@QueryParam("page") @DefaultValue("1") Integer page,
                      @QueryParam("size") @DefaultValue("10") Integer size,
                      @QueryParam("name") String name,
                      @QueryParam("description") String description);

    @GET
    @Path("/{id}")
    @ApiOperation("Получить роль по идентификатору")
    @ApiResponse(code = 200, message = "Роли")
    Role getById(@PathParam("id") Integer id);


    @POST
    @Path("/create")
    @ApiOperation("Создать роль")
    @ApiResponse(code = 200, message = "Созданная роль")
    Role create(Role role);

    @PUT
    @Path("/update")
    @ApiOperation("Изменить роль")
    @ApiResponse(code = 200, message = "Измененная роль")
    Role update(Role role);

    @DELETE
    @Path("/delete/{id}")
    @ApiOperation("Удалить роль")
    @ApiResponse(code = 200, message = "Роль удалена")
    void delete(@PathParam("id") Integer id);

}


