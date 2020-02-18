package net.n2oapp.security.admin.rest.api.criteria;

import io.swagger.annotations.ApiParam;
import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;

import javax.ws.rs.QueryParam;

public class RestOrganizationCriteria extends OrganizationCriteria {
    @QueryParam("shortName")
    @Override
    @ApiParam(value = "Краткое наименование организации")
    public void setShortName(String shortName) {
        super.setShortName(shortName);
    }

    @QueryParam("name")
    @Override
    @ApiParam(value = "Полное или краткое наименование организации")
    public void setName(String name) {
        super.setName(name);
    }

    @QueryParam("ogrn")
    @Override
    @ApiParam(value = "ОГРН организации")
    public void setOgrn(String ogrn) {
        super.setOgrn(ogrn);
    }

    @QueryParam("systemCode")
    @Override
    @ApiParam(value = "Код системы")
    public void setSystemCode(String systemCode) {
        super.setSystemCode(systemCode);
    }
}
