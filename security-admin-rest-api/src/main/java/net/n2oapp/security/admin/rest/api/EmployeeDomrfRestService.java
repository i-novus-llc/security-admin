package net.n2oapp.security.admin.rest.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.model.EmployeeDomrf;
import net.n2oapp.security.admin.rest.api.criteria.RestEmployeeDomrfCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

/**
 * REST сервис управления уполномоченными лицами ДОМ.РФ
 */
@Path("/employee/domrf")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("REST сервис регистрации пользователей")
public interface EmployeeDomrfRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти всех уполномоченных лиц ДОМ.РФ")
    @ApiResponse(code = 200, message = "Страница уполномоченных лиц ДОМ.РФ")
    Page<EmployeeDomrf> findByDepartment(@BeanParam RestEmployeeDomrfCriteria criteria);

    @GET
    @Path("/{employeeDomrfId}")
    @ApiOperation("Уполномоченное лицо ДОМ.РФ")
    @ApiResponse(code = 200, message = "Уполномоченное лицо ДОМ.РФ")
    EmployeeDomrf get(@PathParam("employeeDomrfId") UUID employeeDomrfId);
}
