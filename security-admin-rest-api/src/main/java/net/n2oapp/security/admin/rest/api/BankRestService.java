package net.n2oapp.security.admin.rest.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.model.bank.Bank;
import net.n2oapp.security.admin.api.model.bank.BankCreateForm;
import net.n2oapp.security.admin.api.model.bank.BankUpdateForm;
import net.n2oapp.security.admin.rest.api.criteria.RestBankCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

/**
 * REST сервис управления банками
 */
@Path("/banks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("REST сервис управления сведениями банка")
public interface BankRestService {

    @GET
    @Path("/")
    @ApiOperation("Поиск сведений всех банков")
    @ApiResponse(code = 200, message = "Страница сведения о банке")
    Page<Bank> findAll(@BeanParam RestBankCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получить сведения о банке по идентификатору")
    @ApiResponse(code = 200, message = "Сведения о банке")
    Bank getById(@PathParam("id") UUID id);


    @POST
    @Path("/")
    @ApiOperation("Создание данных о банке")
    @ApiResponse(code = 200, message = "Созданное сведение о банке")
    Bank create(BankCreateForm bank);

    @PUT
    @Path("/")
    @ApiOperation("Изменения данных о банке")
    @ApiResponse(code = 200, message = "Измененные сведения о банке ")
    Bank update(BankUpdateForm bank);


}
