package net.n2oapp.security.auth;

import net.n2oapp.framework.api.context.ContextEngine;
import net.n2oapp.framework.api.test.TestContextEngine;
import net.n2oapp.framework.api.user.UserContext;
import net.n2oapp.security.auth.common.OauthUser;
import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class SecuritySimplePermissionApiTest {
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    private final SecuritySimplePermissionApi permissionApi = new SecuritySimplePermissionApi();
    private final ContextEngine contextEngine = new TestContextEngine();
    private final UserContext userContext = new UserContext(contextEngine);

    @BeforeEach
    public void init() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void hasPermission() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        PermissionGrantedAuthority permissionGrantedAuthority = new PermissionGrantedAuthority("testPermission");
        authorities.add(permissionGrantedAuthority);
        OauthUser user = new OauthUser("testUser", authorities, oidcIdToken());
        Mockito.doReturn(user).when(authentication).getPrincipal();
        boolean status = permissionApi.hasPermission(userContext, "permission");
        assertFalse(status);
        status = permissionApi.hasPermission(userContext, "testPermission");
        assertTrue(status);
    }

    @Test
    public void hasRole() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        RoleGrantedAuthority roleGrantedAuthority = new RoleGrantedAuthority("testRole");
        authorities.add(roleGrantedAuthority);
        OauthUser user = new OauthUser("testUser", authorities, oidcIdToken());
        Mockito.doReturn(user).when(authentication).getPrincipal();
        boolean status = permissionApi.hasRole(userContext, "role");
        assertFalse(status);
        status = permissionApi.hasRole(userContext, "testRole");
        assertTrue(status);
    }

    @Test
    public void hasAuthentication() {
        OauthUser user = new OauthUser("testUser", oidcIdToken());
        Mockito.doReturn(user).when(authentication).getPrincipal();
        boolean status = permissionApi.hasAuthentication(userContext);
        assertTrue(status);
    }

    @Test
    public void hasUsername() {
        OauthUser user = new OauthUser("testUser", oidcIdToken());
        Mockito.doReturn(user).when(authentication).getPrincipal();
        boolean status = permissionApi.hasUsername(userContext, "user");
        assertFalse(status);
        status = permissionApi.hasUsername(userContext, "testUser");
        assertTrue(status);
    }

    private OidcIdToken oidcIdToken() {
        return new OidcIdToken("token_value", Instant.MIN, Instant.MAX, Map.of("sub", "test"));
    }
}