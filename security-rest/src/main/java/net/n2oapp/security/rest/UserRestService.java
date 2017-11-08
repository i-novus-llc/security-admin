package net.n2oapp.security.rest;



import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.springframework.data.domain.Page;

/**
        * REST сервис регистрации участников оборота семян
        */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("REST сервис регистрации участников оборота семян")
public interface UserRestService {

    @GET
    @Path("/")
    @ApiOperation("Фильтрация юзеров")
    @ApiResponse(code = 200, message = "Юзеры")
    Page <User> search(UserCriteria criteria);
}
