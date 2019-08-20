package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.api.model.ApplicationForm;
import net.n2oapp.security.admin.rest.api.criteria.RestApplicationCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления приложениями
 */
@Path("/applications")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("Приложения")
public interface ApplicationRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти приложение по критериям поиска")
    @ApiResponse(code = 200, message = "Страница приложений")
    Page<Application> findAll(@BeanParam RestApplicationCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получить приложение по идентификатору")
    @ApiResponse(code = 200, message = "Приложения")
    Application getById(@ApiParam(value = "Код приложения") @PathParam("id") String code);

    @POST
    @Path("/")
    @ApiOperation("Создать приложение")
    @ApiResponse(code = 200, message = "Созданное приложение")
    Application create(@ApiParam(value = "Приложение") ApplicationForm serviceForm);

    @PUT
    @Path("/")
    @ApiOperation("Изменить приложение")
    @ApiResponse(code = 200, message = "Измененное приложение")
    Application update(@ApiParam(value = "Приложение") ApplicationForm serviceForm);

    @DELETE
    @Path("/{code}")
    @ApiOperation("Удалить приложение")
    @ApiResponse(code = 200, message = "Приложение удалена")
    void delete(@ApiParam(value = "Код приложения") @PathParam("code") String code);

}


