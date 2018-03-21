package net.n2oapp.security.admin.rest.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.springframework.data.domain.Page;

/**
 * REST сервис управления пользоватлями
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("REST сервис регистрации пользователей")
public interface UserRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти всех пользователей")
    @ApiResponse(code = 200, message = "Страница пользователей")
    Page<User> search(@QueryParam("page") @DefaultValue("1") Integer page,
                      @QueryParam("size") @DefaultValue("10") Integer size,
                      @QueryParam("username") String username, @QueryParam("name") String name,
                      @QueryParam("surname") String surname, @QueryParam("patronymic") String patronymic,
                      @QueryParam("isActive") Boolean isActive);

    @GET
    @Path("/{id}")
    @ApiOperation("Получить пользователя по идентификатору")
    @ApiResponse(code = 200, message = "Пользователь")
    User getById(@PathParam("id") Integer id);


    @POST
    @Path("/create")
    @ApiOperation("Создать пользователя")
    @ApiResponse(code = 200, message = "Созданный пользователь")
    User create(User user);

    @PUT
    @Path("/update")
    @ApiOperation("Изменить пользователя")
    @ApiResponse(code = 200, message = "Измененный пользователь")
    User update(User user);

    @DELETE
    @Path("/delete/{id}")
    @ApiOperation("Удалить пользователя")
    @ApiResponse(code = 200, message = "Пользователь удален")
    void delete(@PathParam("id") Integer id);

}
