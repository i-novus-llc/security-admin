package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.*;
import net.n2oapp.security.admin.api.model.AccountType;
import net.n2oapp.security.admin.rest.api.criteria.AccountTypeRestCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления типами аккаунтов
 */
@Path("/accountTypes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Типы аккаунтов", authorizations = @Authorization(value = "oauth2"))
public interface AccountTypeRestService {
    @GET
    @Path("/")
    @ApiOperation("Все типы аккаунтов")
    @ApiResponse(code = 200, message = "Найти типы аккаунтов по критериям поиска")
    Page<AccountType> findAll(@BeanParam AccountTypeRestCriteria criteria);

    @GET
    @Path("/{accountTypeId}")
    @ApiOperation("Получить тип аккаунта по идентификатору")
    @ApiResponse(code = 200, message = "Найденный тип аккаунта")
    AccountType findById(@ApiParam(value = "Идентификатор типа аккаунта") @PathParam("accountTypeId") Integer accountTypeId);

    @POST
    @Path("/")
    @ApiOperation("Создать тип аккаунта")
    @ApiResponse(code = 200, message = "Созданный тип аккаунта")
    AccountType create(@ApiParam(value = "Тип аккаунта") AccountType accountType);

    @PUT
    @Path("/")
    @ApiOperation("Изменить тип аккаунта")
    @ApiResponse(code = 200, message = "Измененный тип аккаунта")
    AccountType update(@ApiParam(value = "Тип аккаунта") AccountType accountType);

    @DELETE
    @Path("/{accountTypeId}")
    @ApiOperation("Удалить тип аккаунта")
    @ApiResponse(code = 204, message = "Тип аккаунта удален")
    void delete(@ApiParam(value = "Идентификатор типа аккаунта") @PathParam("accountTypeId") Integer accountTypeId);
}
