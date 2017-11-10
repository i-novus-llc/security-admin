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
    Page <User> search(UserCriteria criteria);

    @POST
    @Path("/users.main.create")
    @ApiOperation("Создание юзеров")
    @ApiResponse(code = 200, message = "Юзеры")
    void create(User user);

    @PUT
    @Path("/users.main.update")
    @ApiOperation("Изменение юзеров")
    @ApiResponse(code = 200, message = "Юзеры")
    void update(Integer id);

    @DELETE
    @Path("/users.main.delete")
    @ApiOperation("Удаление юзеров")
    @ApiResponse(code = 200, message = "Юзеры")
    void  delete(Integer id);



}
