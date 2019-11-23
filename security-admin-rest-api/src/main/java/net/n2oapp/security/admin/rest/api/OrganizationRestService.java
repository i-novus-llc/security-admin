package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.api.model.Organization;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис организациями
 */
@Path("/organization")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Организации", authorizations = @Authorization(value = "oauth2"))
public interface OrganizationRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти все организации")
    @ApiResponse(code = 200, message = "Страница организации")
    Page<Organization> getAll(@BeanParam OrganizationCriteria criteria);
}
