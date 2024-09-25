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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.rest.api.criteria.RestRegionCriteria;
import org.springframework.data.domain.Page;

/**
 * REST сервис управления регионами
 */
@Path("/region")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Регионы", authorizations = @Authorization(value = "oauth2"))
public interface RegionRestService {

    @GET
    @Path("/")
    @ApiOperation("Найти все регионы")
    @ApiResponse(code = 200, message = "Страница регионов")
    Page<Region> getAll(@BeanParam RestRegionCriteria criteria);
}
