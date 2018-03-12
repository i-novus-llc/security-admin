package net.n2oapp.security.rest.api;



import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
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
    @ApiOperation("Фильтрация юзеров")
    @ApiResponse(code = 200, message = "Юзеры")
    Page <User> search(@QueryParam("page") @DefaultValue("1") Integer page, @QueryParam("size")  @DefaultValue("10") Integer size,
                       @QueryParam("username") String username,@QueryParam("name") String name,
                       @QueryParam("surname") String surname,@QueryParam("patronymic") String patronymic,
                       @QueryParam("isActive") Boolean isActive);
    @GET
    @Path("/{id}")
    @ApiOperation("Получение юзеров")
    @ApiResponse(code = 200, message = "Юзеры")
    User getById(@PathParam("id") Integer id);


    @POST
    @Path("/create")
    @ApiOperation("Создание юзеров")
    @ApiResponse(code = 200, message = "Юзеры")
    User create(User user);

    @PUT
    @Path("/update")
    @ApiOperation("Изменение юзеров")
    @ApiResponse(code = 200, message = "Юзеры")
    User update(User user);

    @DELETE
    @Path("/delete/{id}")
    @ApiOperation("Удаление юзеров")
    @ApiResponse(code = 200, message = "Юзеры")
    void  delete(@PathParam("id") Integer id);

}
