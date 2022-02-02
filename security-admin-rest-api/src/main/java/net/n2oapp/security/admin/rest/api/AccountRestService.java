package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.*;
import net.n2oapp.security.admin.api.model.Account;
import net.n2oapp.security.admin.rest.api.criteria.RestAccountCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления аккаунтами
 */
@Path("/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Аккаунты", authorizations = @Authorization(value = "oauth2"))
public interface AccountRestService {
    @GET
    @Path("/")
    @ApiOperation("Все аккаунты")
    @ApiResponse(code = 200, message = "Найти аккаунты по критериям поиска")
    Page<Account> findAll(@BeanParam RestAccountCriteria criteria);

    @GET
    @Path("/{accountId}")
    @ApiOperation("Получить аккаунт по идентификатору")
    @ApiResponse(code = 200, message = "Найденный аккаунт")
    Account findById(@ApiParam(value = "Идентификатор аккаунта") @PathParam("accountId") Integer accountId);

    @POST
    @Path("/")
    @ApiOperation("Создать аккаунт")
    @ApiResponse(code = 200, message = "Созданный аккаунт")
    Account create(@ApiParam(value = "Аккаунт") Account accountType);

    @PUT
    @Path("/")
    @ApiOperation("Изменить аккаунт")
    @ApiResponse(code = 200, message = "Измененный аккаунт")
    Account update(@ApiParam(value = "Аккаунт") Account accountType);

    @DELETE
    @Path("/{accountId}")
    @ApiOperation("Удалить аккаунт")
    @ApiResponse(code = 204, message = "Аккаунт удален")
    void delete(@ApiParam(value = "Идентификатор аккаунта") @PathParam("accountId") Integer accountId);
}
