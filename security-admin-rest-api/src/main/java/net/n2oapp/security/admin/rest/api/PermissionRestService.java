package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.rest.api.criteria.RestBaseCriteria;
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
public interface PermissionRestService extends PermissionService<RestBaseCriteria> {

    @GET
    @Path("/")
    @ApiOperation("Найти все права доступа")
    @ApiResponse(code = 200, message = "Страница прав доступа")
    Page<Permission> findAll(@BeanParam RestBaseCriteria criteria);

    @Override
    default Permission create(Permission permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Permission update(Permission permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void delete(Integer id) {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{id}")
    @ApiOperation("Получить право доступа по идентификатору")
    @ApiResponse(code = 200, message = "Права доступа")
    Permission getById(@PathParam("id") Integer id);
}
