/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.n2oapp.security.admin.rest.api;

import io.swagger.annotations.*;
import net.n2oapp.security.admin.api.model.OrgCategory;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.rest.api.criteria.RestOrgCategoryCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestOrganizationCriteria;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST сервис для чтения организаций
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Организации", authorizations = @Authorization(value = "oauth2"))
public interface OrganizationReadRestService {

    @GET
    @Path("/organizations")
    @ApiOperation("Найти все организации")
    @ApiResponse(code = 200, message = "Страница организации")
    Page<Organization> getAll(@BeanParam RestOrganizationCriteria criteria);

    @GET
    @Path("/organizations/{id}")
    @ApiOperation("Найти организацию по уникальному идентификатору")
    @ApiResponse(code = 200, message = "Организация")
    Organization get(@ApiParam("Уникальный идентификатор записи") @PathParam("id") Integer id);


    @GET
    @Path("/orgCategories")
    @ApiOperation("Найти все категории организаций")
    @ApiResponse(code = 200, message = "Страница категорий организаций")
    Page<OrgCategory> getAllCategories(@BeanParam RestOrgCategoryCriteria criteria);
}
