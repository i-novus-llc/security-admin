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
package net.n2oapp.security.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import net.n2oapp.framework.access.data.SecurityProvider;
import net.n2oapp.framework.access.metadata.Security;
import net.n2oapp.framework.access.metadata.SecurityObject;
import net.n2oapp.framework.access.metadata.accesspoint.model.N2oUrlAccessPoint;
import net.n2oapp.framework.access.metadata.schema.AccessContext;
import net.n2oapp.framework.access.metadata.schema.CompiledAccessSchema;
import net.n2oapp.framework.access.metadata.schema.permission.N2oPermission;
import net.n2oapp.framework.access.metadata.schema.role.N2oRole;
import net.n2oapp.framework.access.metadata.schema.simple.SimpleCompiledAccessSchema;
import net.n2oapp.framework.access.metadata.schema.user.N2oUserAccess;
import net.n2oapp.framework.access.simple.PermissionAndRoleCollector;
import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.framework.api.user.StaticUserContext;
import net.n2oapp.framework.config.compile.pipeline.N2oPipelineSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static net.n2oapp.framework.access.simple.PermissionAndRoleCollector.URL_ACCESS;

public class N2oUrlFilter implements Filter {

    private final String schemaId;
    private final Boolean defaultUrlAccessDenied;
    private final MetadataEnvironment environment;
    private final SecurityProvider securityProvider;

    public N2oUrlFilter(String schemaId, Boolean defaultUrlAccessDenied, MetadataEnvironment environment, SecurityProvider securityProvider) {
        this.schemaId = schemaId;
        this.defaultUrlAccessDenied = defaultUrlAccessDenied;
        this.environment = environment;
        this.securityProvider = securityProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String pathUrl = req.getServletPath() + (req.getPathInfo() == null ? "" : req.getPathInfo());
        AccessContext context = new AccessContext(schemaId);
        CompiledAccessSchema schema = environment.getReadCompileBindTerminalPipelineFunction()
                .apply(new N2oPipelineSupport(environment))
                .get(context, null);
        Security urlSecurity = collectUrlAccess(pathUrl, (SimpleCompiledAccessSchema) schema);
        securityProvider.checkAccess(urlSecurity, StaticUserContext.getUserContext());
        chain.doFilter(req, response);
    }

    private Security collectUrlAccess(String url, SimpleCompiledAccessSchema schema) {
        Security security = new Security();
        SecurityObject securityObject = new SecurityObject();
        if (schema.getPermitAllPoints() != null) {
            schema.getPermitAllPoints().stream()
                    .filter(ap -> ap instanceof N2oUrlAccessPoint
                            && ((N2oUrlAccessPoint) ap).getMatcher().matches(url))
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> {
                                if (list.size() == 1) {
                                    securityObject.setPermitAll(true);
                                }
                                return list;
                            }
                    ));
        }

        if (schema.getAuthenticatedPoints() != null) {
            schema.getAuthenticatedPoints().stream()
                    .filter(ap -> ap instanceof N2oUrlAccessPoint
                            && ((N2oUrlAccessPoint) ap).getMatcher().matches(url))
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> {
                                if (list.size() == 1) {
                                    securityObject.setAuthenticated(true);
                                }
                                return list;
                            }
                    ));
        }

        if (schema.getAnonymousPoints() != null) {
            schema.getAnonymousPoints().stream()
                    .filter(ap -> ap instanceof N2oUrlAccessPoint
                            && ((N2oUrlAccessPoint) ap).getMatcher().matches(url))
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> {
                                if (list.size() == 1) {
                                    securityObject.setAnonymous(true);
                                }
                                return list;
                            }
                    ));
        }

        List<N2oRole> roles = PermissionAndRoleCollector.collectRoles(N2oUrlAccessPoint.class,
                URL_ACCESS.apply(url), schema);
        if (roles != null && roles.size() > 0) {
            securityObject.setRoles(
                    roles
                            .stream()
                            .map(N2oRole::getId)
                            .collect(Collectors.toSet())
            );
        }

        List<N2oPermission> permissions = PermissionAndRoleCollector.collectPermission(N2oUrlAccessPoint.class,
                URL_ACCESS.apply(url), schema);
        if (permissions != null && permissions.size() > 0) {
            securityObject.setPermissions(
                    permissions
                            .stream()
                            .map(N2oPermission::getId)
                            .collect(Collectors.toSet())
            );
        }

        List<N2oUserAccess> userAccesses = PermissionAndRoleCollector.collectUsers(N2oUrlAccessPoint.class,
                URL_ACCESS.apply(url), schema);
        if (userAccesses != null && userAccesses.size() > 0) {
            securityObject.setUsernames(
                    userAccesses
                            .stream()
                            .map(N2oUserAccess::getId)
                            .collect(Collectors.toSet())
            );
        }

        if (securityObject.isEmpty()) {
            securityObject.setPermitAll(!defaultUrlAccessDenied);
            securityObject.setDenied(defaultUrlAccessDenied);
        }
        if (security.isEmpty())
            security.add(new HashMap<>());
        security.get(0).put("url", securityObject);
        return security;
    }
}
