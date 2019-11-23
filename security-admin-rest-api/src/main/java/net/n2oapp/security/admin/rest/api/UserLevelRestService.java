package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
import net.n2oapp.security.admin.api.model.UserLevel;
import org.springframework.data.domain.Page;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления уровнями пользователя
 */
@Path("/userLevels")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Уровни пользователя", authorizations = @Authorization(value = "oauth2"))
public interface UserLevelRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти все уровни пользователя")
    @ApiResponse(code = 200, message = "Страница уровней пользователя")
    Page<UserLevel> getAll();

    @GET
    @Path("/forFilter")
    @ApiOperation("Найти все уровни пользователя для фильтра")
    @ApiResponse(code = 200, message = "Страница уровней пользователя")
    Page<UserLevel> getAllForFilter();
}
