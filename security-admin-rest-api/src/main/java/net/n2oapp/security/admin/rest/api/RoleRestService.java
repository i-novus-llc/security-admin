package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.rest.api.criteria.RestRoleCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * REST сервис управления ролями  пользователей
 */
@Path("/roles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("REST сервис регистрации ролей  пользователей")
public interface RoleRestService extends RoleService<RestRoleCriteria> {

    @GET
    @Path("/")
    @ApiOperation("Найти роли по критериям поиска")
    @ApiResponse(code = 200, message = "Страница ролей")
    Page<Role> findAll(@BeanParam RestRoleCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получить роль по идентификатору")
    @ApiResponse(code = 200, message = "Роли")
    Role getById(@PathParam("id") Integer id);

    @POST
    @Path("/")
    @ApiOperation("Создать роль")
    @ApiResponse(code = 200, message = "Созданная роль")
    Role create(RoleForm role);

    @PUT
    @Path("/")
    @ApiOperation("Изменить роль")
    @ApiResponse(code = 200, message = "Измененная роль")
    Role update(RoleForm role);

    @DELETE
    @Path("/{id}")
    @ApiOperation("Удалить роль")
    @ApiResponse(code = 200, message = "Роль удалена")
    void delete(@PathParam("id") Integer id);

}


