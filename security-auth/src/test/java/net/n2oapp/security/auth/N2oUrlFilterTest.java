package net.n2oapp.security.auth;

import jakarta.servlet.ServletException;
import net.n2oapp.framework.access.data.SecurityProvider;
import net.n2oapp.framework.access.metadata.Security;
import net.n2oapp.framework.access.metadata.SecurityObject;
import net.n2oapp.framework.access.metadata.accesspoint.AccessPoint;
import net.n2oapp.framework.access.metadata.accesspoint.model.N2oUrlAccessPoint;
import net.n2oapp.framework.access.metadata.schema.permission.N2oPermission;
import net.n2oapp.framework.access.metadata.schema.role.N2oRole;
import net.n2oapp.framework.access.metadata.schema.simple.SimpleCompiledAccessSchema;
import net.n2oapp.framework.access.metadata.schema.user.N2oUserAccess;
import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.framework.api.metadata.pipeline.PipelineFunction;
import net.n2oapp.framework.api.metadata.pipeline.ReadCompileBindTerminalPipeline;
import net.n2oapp.security.auth.common.OauthUser;
import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class)
public class N2oUrlFilterTest {

    @Mock
    private MetadataEnvironment environment;
    @Mock
    PipelineFunction<ReadCompileBindTerminalPipeline> pipelineFunctionMock;
    @Mock
    ReadCompileBindTerminalPipeline readCompileBindTerminalPipeline;
    SimpleCompiledAccessSchema schema = new SimpleCompiledAccessSchema();

    @MockBean
    private Authentication authentication;
    @MockBean
    private SecurityContext securityContext;

    @Spy
    private final SecurityProvider securityProvider = new SecurityProvider(new SecuritySimplePermissionApi(), false);
    @Captor
    ArgumentCaptor<Security> securityCaptor;

    @Mock
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final MockFilterChain chain = new MockFilterChain();

    private N2oUrlFilter filter;

    public static final String KEY = "url";

    @BeforeEach
    public void before() {
        Mockito.doReturn(pipelineFunctionMock).when(environment).getReadCompileBindTerminalPipelineFunction();
        Mockito.doReturn(readCompileBindTerminalPipeline).when(pipelineFunctionMock).apply(any());
        Mockito.doReturn(schema).when(readCompileBindTerminalPipeline).get(any(), any());
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        filter = new N2oUrlFilter("default", false, environment, securityProvider);
    }

    @Test
    public void doFilter_permit_all() {
        try {
            List<AccessPoint> accessPoints = Arrays.asList(obtainAccessPoints("http://test.test/permit_all"));
            schema.setPermitAllPoints(accessPoints);

            Mockito.doReturn("http://test.test/permit_all").when(request).getServletPath();
            filter.doFilter(request, response, chain);
            Mockito.verify(securityProvider).checkAccess(securityCaptor.capture(), any());

            SecurityObject securityObject = securityCaptor.getValue().get(0).get(KEY);
            assertTrue(securityObject.getPermitAll());
        } catch (IOException | ServletException e) {
            fail();
        }
    }

    @Test
    public void doFilter_authenticated() {
        try {
            List<AccessPoint> accessPoints = Arrays.asList(obtainAccessPoints("http://test.test/authenticated"));
            schema.setAuthenticatedPoints(accessPoints);

            OauthUser user = new OauthUser("testUser", oidcIdToken());
            Mockito.doReturn(user).when(authentication).getPrincipal();
            Mockito.doReturn("http://test.test/authenticated").when(request).getServletPath();
            filter.doFilter(request, response, chain);
            Mockito.verify(securityProvider).checkAccess(securityCaptor.capture(), any());

            SecurityObject securityObject = securityCaptor.getValue().get(0).get(KEY);
            assertTrue(securityObject.getAuthenticated());
        } catch (IOException | ServletException e) {
            fail();
        }
    }

    @Test
    public void doFilter_anonymous() {
        try {
            List<AccessPoint> accessPoints = Arrays.asList(obtainAccessPoints("http://test.test/annonymous"));
            schema.setAnonymousPoints(accessPoints);

            Mockito.doReturn("http://test.test/annonymous").when(request).getServletPath();
            filter.doFilter(request, response, chain);
            Mockito.verify(securityProvider).checkAccess(securityCaptor.capture(), any());

            SecurityObject securityObject = securityCaptor.getValue().get(0).get(KEY);
            assertTrue(securityObject.getAnonymous());
        } catch (IOException | ServletException e) {
            fail();
        }
    }

    @Test
    public void doFilter_role() {
        try {
            AccessPoint[] accessPoints = obtainAccessPoints("http://test.test/role");
            N2oRole n2oRole = new N2oRole();
            n2oRole.setAccessPoints(accessPoints);
            n2oRole.setId("testAccessRole");
            n2oRole.setName("testAccessRole");
            List<N2oRole> n2oRoles = new ArrayList<>();
            n2oRoles.add(n2oRole);
            schema.setN2oRoles(n2oRoles);

            RoleGrantedAuthority roleGrantedAuthority = new RoleGrantedAuthority("testAccessRole");
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            grantedAuthorities.add(roleGrantedAuthority);

            OauthUser user = new OauthUser("testUser", grantedAuthorities, oidcIdToken());
            Mockito.doReturn(user).when(authentication).getPrincipal();
            Mockito.doReturn("http://test.test/role").when(request).getServletPath();
            filter.doFilter(request, response, chain);
            Mockito.verify(securityProvider).checkAccess(securityCaptor.capture(), any());

            SecurityObject securityObject = securityCaptor.getValue().get(0).get(KEY);

            Set<String> roles = securityObject.getRoles();
            assertEquals("testAccessRole", roles.iterator().next());
        } catch (IOException | ServletException e) {
            fail();
        }
    }

    @Test
    public void doFilter_permission() {
        try {
            AccessPoint[] accessPoints = obtainAccessPoints("http://test.test/permission");
            N2oPermission n2oPermission = new N2oPermission();
            n2oPermission.setAccessPoints(accessPoints);
            n2oPermission.setId("testAccessPermission");
            n2oPermission.setName("testAccessPermission");
            List<N2oPermission> n2oPermissions = new ArrayList<>();
            n2oPermissions.add(n2oPermission);
            schema.setN2oPermissions(n2oPermissions);

            PermissionGrantedAuthority permissionGrantedAuthority = new PermissionGrantedAuthority("testAccessPermission");
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            grantedAuthorities.add(permissionGrantedAuthority);

            OauthUser user = new OauthUser("testUser", grantedAuthorities, oidcIdToken());
            Mockito.doReturn(user).when(authentication).getPrincipal();
            Mockito.doReturn("http://test.test/permission").when(request).getServletPath();
            filter.doFilter(request, response, chain);
            Mockito.verify(securityProvider).checkAccess(securityCaptor.capture(), any());

            SecurityObject securityObject = securityCaptor.getValue().get(0).get(KEY);

            Set<String> permissions = securityObject.getPermissions();
            assertEquals("testAccessPermission", permissions.iterator().next());
        } catch (IOException | ServletException e) {
            fail();
        }
    }

    @Test
    public void doFilter_user() {
        try {
            AccessPoint[] accessPoints = obtainAccessPoints("http://test.test/user");
            N2oUserAccess userAccess = new N2oUserAccess();
            userAccess.setAccessPoints(accessPoints);
            userAccess.setId("testUser");
            List<N2oUserAccess> n2oUserAccesses = new ArrayList<>();
            n2oUserAccesses.add(userAccess);
            schema.setN2oUserAccesses(n2oUserAccesses);

            OauthUser user = new OauthUser("testUser", oidcIdToken());
            Mockito.doReturn(user).when(authentication).getPrincipal();
            Mockito.doReturn("http://test.test/user").when(request).getServletPath();
            filter.doFilter(request, response, chain);
            Mockito.verify(securityProvider).checkAccess(securityCaptor.capture(), any());

            SecurityObject securityObject = securityCaptor.getValue().get(0).get(KEY);

            Set<String> usernames = securityObject.getUsernames();
            assertEquals("testUser", usernames.iterator().next());
        } catch (IOException | ServletException e) {
            fail();
        }
    }

    @Test
    public void doFilter_empty() {
        try {
            Mockito.doReturn("http://test.test/empty").when(request).getServletPath();
            filter.doFilter(request, response, chain);
            Mockito.verify(securityProvider).checkAccess(securityCaptor.capture(), any());

            SecurityObject securityObject = securityCaptor.getValue().get(0).get(KEY);
            assertTrue(securityObject.getPermitAll());
        } catch (IOException | ServletException e) {
            fail();
        }
    }

    private AccessPoint[] obtainAccessPoints(String urlPattern) {
        N2oUrlAccessPoint urlAccessPoint = new N2oUrlAccessPoint();
        urlAccessPoint.setPattern(urlPattern);
        AccessPoint[] accessPoints = new AccessPoint[1];
        accessPoints[0] = urlAccessPoint;
        return accessPoints;
    }

    private OidcIdToken oidcIdToken() {
        return new OidcIdToken("token_value", Instant.MIN, Instant.MAX, Map.of("sub", "test"));
    }
}