package net.n2oapp.security.admin.rest.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.model.EmployeeBank;
import net.n2oapp.security.admin.api.model.EmployeeBankForm;
import net.n2oapp.security.admin.rest.api.criteria.RestEmployeeBankCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

/**
 * REST сервис управления уполномоченными лицами банка
 */
@Path("/employee/bank")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("REST сервис регистрации пользователей")
public interface EmployeeBankRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти всех уполномоченных лиц банка")
    @ApiResponse(code = 200, message = "Страница уполномоченных лиц банка")
    Page<EmployeeBank> findByBank(@BeanParam RestEmployeeBankCriteria criteria);

    @POST
    @Path("/")
    @ApiOperation("Создать уполномоченное лицо банка")
    @ApiResponse(code = 200, message = "Созданный уполномоченное лицо банка")
    EmployeeBank create(EmployeeBankForm user);

    @GET
    @Path("/{employeeBankId}")
    @ApiOperation("Уполномоченное лицо банка")
    @ApiResponse(code = 200, message = "Уполномоченное лицо банка")
    EmployeeBank get(@PathParam("employeeBankId") UUID employeeBankId);
}