package net.n2oapp.security.admin.rest.api.criteria;

import io.swagger.annotations.ApiParam;
import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;

import javax.ws.rs.QueryParam;
import java.util.List;

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

    @QueryParam("systemCodes")
    @Override
    @ApiParam(value = "Коды систем")
    public void setSystemCodes(List<String> systemCodes) {
        super.setSystemCodes(systemCodes);
    }
}
