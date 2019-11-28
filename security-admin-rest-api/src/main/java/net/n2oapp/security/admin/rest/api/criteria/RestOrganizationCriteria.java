package net.n2oapp.security.admin.rest.api.criteria;

import io.swagger.annotations.ApiParam;
import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;

import javax.ws.rs.QueryParam;

public class RestOrganizationCriteria extends OrganizationCriteria {
    @QueryParam("shortName")
    @Override
    @ApiParam(value = "Наименование организации")
    public void setShortName(String shortName) {
        super.setShortName(shortName);
    }
}
