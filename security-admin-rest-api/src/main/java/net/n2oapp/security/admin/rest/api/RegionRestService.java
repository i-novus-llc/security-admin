package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.rest.api.criteria.RestRegionCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления регионами
 */
@Path("/region")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("Регионы")
public interface RegionRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти все регионы")
    @ApiResponse(code = 200, message = "Страница регионов")
    Page<Region> getAll(@BeanParam RestRegionCriteria criteria);
}
