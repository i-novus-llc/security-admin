package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.rest.api.criteria.RestUserDetailsToken;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис для получения информации о пользователе
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("Информация о пользователе")
public interface UserDetailRestService {

    @POST
    @Path("/details")
    @ApiOperation("Загрузить информацию о пользователе, по его имени и списку ролей")
    @ApiResponse(code = 200, message = "Страница пользователей")
    User loadDetails(@ApiParam(value = "Информация о пользователе") RestUserDetailsToken token);
}
