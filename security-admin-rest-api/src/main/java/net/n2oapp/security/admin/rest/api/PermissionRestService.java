package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.model.Permission;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления правами доступа
 */
@Path("/permissions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("REST сервис регистрации прав доступа")
public interface PermissionRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти все права доступа")
    @ApiResponse(code = 200, message = "Страница прав доступа")
    Page<Permission> getAll(@QueryParam("parentId") Integer parentId,
                            @QueryParam("parentIdIsNull") Boolean parentIdIsNull);


    @GET
    @Path("/{id}")
    @ApiOperation("Получить право доступа по идентификатору")
    @ApiResponse(code = 200, message = "Права доступа")
    Permission getById(@PathParam("id") Integer id);

    @POST
    @Path("/")
    @ApiOperation("Создать право доступа")
    @ApiResponse(code = 200, message = "Созданное право доступа")
    Permission create(Permission permission);

    @PUT
    @Path("/")
    @ApiOperation("Изменить право доступа")
    @ApiResponse(code = 200, message = "Измененное право доступа")
    Permission update(Permission permission);

    @DELETE
    @Path("/{id}")
    @ApiOperation("Удалить право доступа")
    @ApiResponse(code = 200, message = "Право доступа удалено")
    void delete(Integer id);
}
