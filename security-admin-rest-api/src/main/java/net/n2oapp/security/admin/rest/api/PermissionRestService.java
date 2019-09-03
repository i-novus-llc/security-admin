package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api("Права доступа")
public interface PermissionRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти все права доступа")
    @ApiResponse(code = 200, message = "Страница прав доступа")
    Page<Permission> getAll(@ApiParam(value = "Код родителя") @QueryParam("parentCode") String parentCode,
                            @ApiParam(value = "Параметр для получения родительских привелегий") @QueryParam("parentCodeIsNull") Boolean parentIdIsNull);


    @GET
    @Path("/{code}")
    @ApiOperation("Получить право доступа по идентификатору")
    @ApiResponse(code = 200, message = "Права доступа")
    Permission getById(@ApiParam(value = "Код") @PathParam("code") String code);

    @POST
    @Path("/")
    @ApiOperation("Создать право доступа")
    @ApiResponse(code = 200, message = "Созданное право доступа")
    Permission create(@ApiParam(value = "Привелегия") Permission permission);

    @PUT
    @Path("/")
    @ApiOperation("Изменить право доступа")
    @ApiResponse(code = 200, message = "Измененное право доступа")
    Permission update(@ApiParam(value = "Привелегия") Permission permission);

    @DELETE
    @Path("/{code}")
    @ApiOperation("Удалить право доступа")
    @ApiResponse(code = 200, message = "Право доступа удалено")
    void delete(@ApiParam(value = "Код") @PathParam("code") String code);
}
