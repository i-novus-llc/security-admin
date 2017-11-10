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
    Page<Role> search(RoleCriteria criteria);


    @POST
    @Path("/roles.main.create")
    @ApiOperation("Создание ролей")
    @ApiResponse(code = 200, message = "Роли")
    void create(Role role);

    @PUT
    @Path("/roles.main.update")
    @ApiOperation("Изменение ролей")
    @ApiResponse(code = 200, message = "Роли")
    void update(Integer id);

    @DELETE
    @Path("/roles.main.delete")
    @ApiOperation("Удаление ролей")
    @ApiResponse(code = 200, message = "Роли")
    void  delete(Integer id);

}


