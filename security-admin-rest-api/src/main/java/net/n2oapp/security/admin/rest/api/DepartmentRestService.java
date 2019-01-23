package net.n2oapp.security.admin.rest.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.model.department.Department;
import net.n2oapp.security.admin.api.model.department.DepartmentCreateForm;
import net.n2oapp.security.admin.api.model.department.DepartmentUpdateForm;
import net.n2oapp.security.admin.rest.api.criteria.RestDepartmentCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис управления подразделениями ДОМ.РФ
 */
@Path("/departments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("REST сервис управления сведениями подразделения ДОМ.РФ")
public interface DepartmentRestService {

    @GET
    @Path("/")
    @ApiOperation("Поиск сведений всех подразделений")
    @ApiResponse(code = 200, message = "Страница сведения о подразделениях")
    Page<Department> findAll(@BeanParam RestDepartmentCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получить сведения о подразделении по идентификатору")
    @ApiResponse(code = 200, message = "Сведения о подразделении")
    Department getById(@PathParam("id") String id);


    @POST
    @Path("/")
    @ApiOperation("Создание подразделения")
    @ApiResponse(code = 200, message = "Созданное сведение о подразделении")
    Department create(DepartmentCreateForm department);

    @PUT
    @Path("/")
    @ApiOperation("Изменения данных о подразделении")
    @ApiResponse(code = 200, message = "Измененные сведения о подразделении ")
    Department update(DepartmentUpdateForm department);


}
