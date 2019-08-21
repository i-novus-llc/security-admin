package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import net.n2oapp.security.admin.rest.api.criteria.RestSystemCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления системами
 */
@Path("/systems")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("Системы")
public interface SystemRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти систему по критериям поиска")
    @ApiResponse(code = 200, message = "Страница систем")
    Page<AppSystem> findAll(@BeanParam RestSystemCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получить систему по идентификатору")
    @ApiResponse(code = 200, message = "Системы")
    AppSystem getById(@ApiParam(value = "Код") @PathParam("id") String code);

    @POST
    @Path("/")
    @ApiOperation("Создать систему")
    @ApiResponse(code = 200, message = "Созданная система")
    AppSystem create(@ApiParam(value = "Система") AppSystemForm systemForm);

    @PUT
    @Path("/")
    @ApiOperation("Изменить систему")
    @ApiResponse(code = 200, message = "Измененная система")
    AppSystem update(@ApiParam(value = "Система")AppSystemForm systemForm);

    @DELETE
    @Path("/{code}")
    @ApiOperation("Удалить систему")
    @ApiResponse(code = 200, message = "Система удалена")
    void delete(@ApiParam(value = "Код системы") @PathParam("code") String code);

}


