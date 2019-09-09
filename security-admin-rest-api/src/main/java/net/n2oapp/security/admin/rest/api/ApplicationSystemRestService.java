package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.rest.api.criteria.RestApplicationCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestSystemCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления приложениями и системами
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("Приложения и Системы")
public interface ApplicationSystemRestService {

    String SYSTEM_PATH = "/systems";
    String APPLICATION_PATH = "/applications";

    @GET
    @Path(APPLICATION_PATH + "/")
    @ApiOperation("Найти приложение по критериям поиска")
    @ApiResponse(code = 200, message = "Страница приложений")
    Page<Application> findAllApplication(@BeanParam RestApplicationCriteria criteria);

    @GET
    @Path(APPLICATION_PATH + "/{id}")
    @ApiOperation("Получить приложение по идентификатору")
    @ApiResponse(code = 200, message = "Приложения")
    Application getApplicationById(@ApiParam(value = "Код приложения") @PathParam("id") String code);

    @POST
    @Path(APPLICATION_PATH + "/")
    @ApiOperation("Создать приложение")
    @ApiResponse(code = 200, message = "Созданное приложение")
    Application createApplication(@ApiParam(value = "Приложение") Application serviceForm);

    @PUT
    @Path(APPLICATION_PATH + "/")
    @ApiOperation("Изменить приложение")
    @ApiResponse(code = 200, message = "Измененное приложение")
    Application updateApplication(@ApiParam(value = "Приложение") Application serviceForm);

    @DELETE
    @Path(APPLICATION_PATH + "/{code}")
    @ApiOperation("Удалить приложение")
    @ApiResponse(code = 200, message = "Приложение удалена")
    void deleteApplication(@ApiParam(value = "Код приложения") @PathParam("code") String code);

    @GET
    @Path(SYSTEM_PATH + "/")
    @ApiOperation("Найти систему по критериям поиска")
    @ApiResponse(code = 200, message = "Страница систем")
    Page<AppSystem> findAllSystem(@BeanParam RestSystemCriteria criteria);

    @GET
    @Path(SYSTEM_PATH + "/{id}")
    @ApiOperation("Получить систему по идентификатору")
    @ApiResponse(code = 200, message = "Системы")
    AppSystem getSystemById(@ApiParam(value = "Код") @PathParam("id") String code);

    @POST
    @Path(SYSTEM_PATH + "/")
    @ApiOperation("Создать систему")
    @ApiResponse(code = 200, message = "Созданная система")
    AppSystem createSystem(@ApiParam(value = "Система") AppSystemForm systemForm);

    @PUT
    @Path(SYSTEM_PATH + "/")
    @ApiOperation("Изменить систему")
    @ApiResponse(code = 200, message = "Измененная система")
    AppSystem updateSystem(@ApiParam(value = "Система") AppSystemForm systemForm);

    @DELETE
    @Path(SYSTEM_PATH + "/{code}")
    @ApiOperation("Удалить систему")
    @ApiResponse(code = 200, message = "Система удалена")
    void deleteSystem(@ApiParam(value = "Код системы") @PathParam("code") String code);

}


