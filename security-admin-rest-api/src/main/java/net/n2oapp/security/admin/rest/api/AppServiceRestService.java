package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.criteria.ServiceCriteria;
import net.n2oapp.security.admin.api.model.AppService;
import net.n2oapp.security.admin.api.model.AppServiceForm;
import net.n2oapp.security.admin.rest.api.criteria.RestServiceCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления службами
 */
@Path("/services")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("REST сервис регистрации служб")
public interface AppServiceRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти службу по критериям поиска")
    @ApiResponse(code = 200, message = "Страница служб")
    Page<AppService> findAll(@BeanParam RestServiceCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получить службу по идентификатору")
    @ApiResponse(code = 200, message = "Службы")
    AppService getById(@PathParam("id") String code);

    @POST
    @Path("/")
    @ApiOperation("Создать службу")
    @ApiResponse(code = 200, message = "Созданная служба")
    AppService create(AppServiceForm serviceForm);

    @PUT
    @Path("/")
    @ApiOperation("Изменить службу")
    @ApiResponse(code = 200, message = "Измененная служба")
    AppService update(AppServiceForm serviceForm);

    @DELETE
    @Path("/{code}")
    @ApiOperation("Удалить службу")
    @ApiResponse(code = 200, message = "Служба удалена")
    void delete(@PathParam("code") String code);

}


