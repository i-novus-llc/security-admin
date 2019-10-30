package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
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
@Api("Роли")
public interface RoleRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти роли по критериям поиска")
    @ApiResponse(code = 200, message = "Страница ролей")
    Page<Role> findAll(@BeanParam RestRoleCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получить роль по идентификатору")
    @ApiResponse(code = 200, message = "Роли")
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
    @ApiResponse(code = 200, message = "Роль удалена")
    void delete(@ApiParam(value = "Идентификатор") @PathParam("id") Integer id);

    @GET
    @Path("/withSystem")
    @ApiOperation("Найти все права доступа сгрупированные по системам")
    @ApiResponse(code = 200, message = "Страница прав доступа")
    Page<Role> findAllWithSystem(@ApiParam(value = "Критерия поиска") @BeanParam RestRoleCriteria criteria);
}


