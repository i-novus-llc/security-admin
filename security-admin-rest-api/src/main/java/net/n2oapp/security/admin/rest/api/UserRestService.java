package net.n2oapp.security.admin.rest.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestUserDetailsToken;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления пользоватлями
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("Пользователи")
public interface UserRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти всех пользователей")
    @ApiResponse(code = 200, message = "Страница пользователей")
    Page<User> findAll(@BeanParam RestUserCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получить пользователя по идентификатору")
    @ApiResponse(code = 200, message = "Пользователь")
    User getById(@ApiParam(value = "Идентификатор") @PathParam("id") Integer id);


    @POST
    @Path("/")
    @ApiOperation("Создать пользователя")
    @ApiResponse(code = 200, message = "Созданный пользователь")
    User create(@ApiParam(value = "Пользователь") UserForm user);

    @PUT
    @Path("/")
    @ApiOperation("Изменить пользователя")
    @ApiResponse(code = 200, message = "Измененный пользователь")
    User update(@ApiParam(value = "Пользователь") UserForm user);

    @DELETE
    @Path("/{id}")
    @ApiOperation("Удалить пользователя")
    @ApiResponse(code = 200, message = "Пользователь удален")
    void delete(@ApiParam(value = "Идентификатор") @PathParam("id") Integer id);

    @PUT
    @Path("/changeActive/{id}")
    @ApiOperation("Изменить статус пользователя")
    @ApiResponse(code = 200, message = "Пользователь с измененным статусом")
    User changeActive(@ApiParam(value = "Идентификатор") @PathParam("id") Integer id);

    @POST
    @Path("/details")
    @ApiOperation("Загрузить информацию о пользователе, по его имени и списку ролей")
    @ApiResponse(code = 200, message = "Страница пользователей")
    User loadDetails(@ApiParam(value = "Информация о пользователе") RestUserDetailsToken token);

    @GET
    @Path("/simpleDetails")
    @ApiOperation("Загрузить простейшую информацию о пользователе (имя, почта и временный пароль)")
    @ApiResponse(code = 200, message = "Временный пароль")
    User loadSimpleDetails(@ApiParam(value = "Идентификатор") @QueryParam("id") Integer id);

    @PUT
    @Path("/resetPassword")
    @ApiOperation("Сбросить пароль")
    @ApiResponse(code = 200, message = "Пароль сброшен")
    void resetPassword(@ApiParam(value = "Пользователь") UserForm user);
}
