package net.n2oapp.security.admin.rest.api.criteria;

import io.swagger.annotations.ApiParam;
import net.n2oapp.security.admin.api.criteria.DepartmentCriteria;

import javax.ws.rs.QueryParam;

public class RestDepartmentCriteria extends DepartmentCriteria {
    @QueryParam("name")
    @Override
    @ApiParam(value = "Наименование департамента")
    public void setName(String name) {
        super.setName(name);
    }
}
