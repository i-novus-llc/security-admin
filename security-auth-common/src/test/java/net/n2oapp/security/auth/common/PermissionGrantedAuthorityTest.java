package net.n2oapp.security.auth.common;

import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static net.n2oapp.security.auth.common.TestConstants.OTHER_PERMISSION;
import static net.n2oapp.security.auth.common.TestConstants.SOME_PERMISSION;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class PermissionGrantedAuthorityTest {

    private static final String DEFAULT_PERMISSION_PREFIX = "PERMISSION_";

    @Test
    public void testGetAuthority() {
        PermissionGrantedAuthority auth = new PermissionGrantedAuthority(SOME_PERMISSION);
        assertEquals(DEFAULT_PERMISSION_PREFIX + SOME_PERMISSION, auth.getAuthority());
    }

    @Test
    public void testGetPermission() {
        assertEquals(SOME_PERMISSION, new PermissionGrantedAuthority(SOME_PERMISSION).getPermission());
    }

    @Test
    public void testToString() {
        assertEquals(SOME_PERMISSION, new PermissionGrantedAuthority(SOME_PERMISSION).toString());
    }

    @Test
    public void testEquals_Symmetric() {
        PermissionGrantedAuthority x = new PermissionGrantedAuthority(SOME_PERMISSION);
        PermissionGrantedAuthority y = new PermissionGrantedAuthority(SOME_PERMISSION);

        assertTrue(x.equals(x) && x.equals(x));
        assertTrue(x.hashCode() == x.hashCode());

        assertTrue(x.equals(y) && y.equals(x));
        assertTrue(x.hashCode() == y.hashCode());

        PermissionGrantedAuthority z = new PermissionGrantedAuthority(OTHER_PERMISSION);

        assertFalse(x.equals(z));
        assertFalse(z.equals(x));
        assertFalse(x.hashCode() == z.hashCode());

        Object otherObject = new Object();

        assertFalse(x.equals(otherObject));
        assertFalse(otherObject.equals(x));
        assertFalse(x.hashCode() == otherObject.hashCode());
    }
}