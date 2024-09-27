package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.n2oapp.security.admin.api.model.Account;
import net.n2oapp.security.admin.rest.api.criteria.RestAccountCriteria;
import org.springframework.data.domain.Page;

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
    @Path("/{id}")
    @ApiOperation("Получить аккаунт по идентификатору")
    @ApiResponse(code = 200, message = "Найденный аккаунт")
    Account findById(@ApiParam(value = "Идентификатор аккаунта") @PathParam("id") Integer accountId);

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
    @Path("/{id}")
    @ApiOperation("Удалить аккаунт")
    @ApiResponse(code = 204, message = "Аккаунт удален")
    void delete(@ApiParam(value = "Идентификатор аккаунта") @PathParam("id") Integer accountId);

    @PUT
    @Path("/changeActive/{id}")
    @ApiOperation("Изменить статус пользователя")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Аккаунт с измененным статусом"),
            @ApiResponse(code = 400, message = "Некорректный запрос. Отсутствуют обязательные поля или заполнены некорректными данными")
    })
    Account changeActive(@ApiParam(value = "Идентификатор аккаунта") @PathParam("id") Integer id);
}
