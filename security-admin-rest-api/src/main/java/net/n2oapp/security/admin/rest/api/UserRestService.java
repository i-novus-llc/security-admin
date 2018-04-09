package net.n2oapp.security.admin.rest.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * REST сервис управления пользоватлями
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("REST сервис регистрации пользователей")
public interface UserRestService extends UserService<RestUserCriteria> {

    @GET
    @Path("/")
    @ApiOperation("Найти всех пользователей")
    @ApiResponse(code = 200, message = "Страница пользователей")
    Page<User> findAll(@BeanParam RestUserCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получить пользователя по идентификатору")
    @ApiResponse(code = 200, message = "Пользователь")
    User getById(@PathParam("id") Integer id);


    @POST
    @Path("/")
    @ApiOperation("Создать пользователя")
    @ApiResponse(code = 200, message = "Созданный пользователь")
    User create(UserForm user);

    @PUT
    @Path("/")
    @ApiOperation("Изменить пользователя")
    @ApiResponse(code = 200, message = "Измененный пользователь")
    User update(UserForm user);

    @DELETE
    @Path("/{id}")
    @ApiOperation("Удалить пользователя")
    @ApiResponse(code = 200, message = "Пользователь удален")
    void delete(@PathParam("id") Integer id);

    @PUT
    @Path("changeActive/{id}")
    @ApiOperation("Изменить статус пользователя")
    @ApiResponse(code = 200, message = "Пользователь с измененным статусом")
    User changeActive(@PathParam("id") Integer id);

    @GET
    @Path("/details/{username}")
    @ApiOperation("Загрузить информацию о пользователе, по его имени и списку ролей")
    @ApiResponse(code = 200, message = "Страница пользователей")
    User loadDetails(@PathParam("username") String username, @QueryParam("rolenames") List<String> roleNames);

}
