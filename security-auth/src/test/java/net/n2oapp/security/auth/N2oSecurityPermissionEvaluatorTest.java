package net.n2oapp.security.auth;

import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class N2oSecurityPermissionEvaluatorTest {

    private final N2oSecurityPermissionEvaluator permissionEvaluator = new N2oSecurityPermissionEvaluator();

    @Test
    public void hasPermission() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        Object mockedPrincipal = new Object();
        Object mockedCredentials = new Object();
        Serializable serializable = Mockito.withSettings().serializable();
        PermissionGrantedAuthority permissionGrantedAuthority = new PermissionGrantedAuthority("testPermission");
        authorities.add(permissionGrantedAuthority);
        Authentication authentication = new TestingAuthenticationToken(mockedPrincipal, mockedCredentials, authorities);

        boolean status = permissionEvaluator.hasPermission(authentication, serializable, "string", new Object());
        assertFalse(status);
        status = permissionEvaluator.hasPermission(authentication, new Object(), 1);
        assertFalse(status);
        status = permissionEvaluator.hasPermission(authentication, new Object(), "wrongPermissionName");
        assertFalse(status);
        status = permissionEvaluator.hasPermission(authentication, new Object(), "testPermission");
        assertTrue(status);
    }
}