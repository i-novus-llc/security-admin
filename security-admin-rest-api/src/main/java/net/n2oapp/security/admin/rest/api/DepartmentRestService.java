package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
import net.n2oapp.security.admin.api.model.Department;
import net.n2oapp.security.admin.rest.api.criteria.RestDepartmentCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис департаментов
 */
@Path("/department")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Департаменты", authorizations = @Authorization(value = "oauth2"))
public interface DepartmentRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти все департаменты")
    @ApiResponse(code = 200, message = "Страница департамента")
    Page<Department> getAll(@BeanParam RestDepartmentCriteria criteria);
}
