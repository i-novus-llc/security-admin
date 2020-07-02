package net.n2oapp.security.auth;

import lombok.AllArgsConstructor;
import lombok.Setter;
import net.n2oapp.framework.access.data.SecurityProvider;
import net.n2oapp.framework.access.metadata.Security;
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

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static net.n2oapp.framework.access.simple.PermissionAndRoleCollector.URL_ACCESS;

@Setter
@AllArgsConstructor
public class N2oUrlFilter implements Filter {

    public static final String USER = "user";
    private String schemaId;
    private Boolean defaultUrlAccessDenied;
    private MetadataEnvironment environment;
    private SecurityProvider securityProvider;

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
        security.setSecurityMap(new HashMap<>());
        Security.SecurityObject securityObject = new Security.SecurityObject();
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
        security.getSecurityMap().put("url", securityObject);
        return security;
    }
}
