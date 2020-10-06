package net.n2oapp.security.auth.common;

import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.junit.Test;

import static net.n2oapp.security.auth.common.TestConstants.OTHER_ROLE_NAME;
import static net.n2oapp.security.auth.common.TestConstants.SOME_ROLE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class RoleGrantedAuthorityTest {

    private static final String DEFAULT_ROLE_PREFIX = "ROLE_";

    @Test
    public void testGetAuthority() {
        RoleGrantedAuthority auth = new RoleGrantedAuthority(SOME_ROLE_NAME);
        assertEquals(DEFAULT_ROLE_PREFIX + SOME_ROLE_NAME, auth.getAuthority());
    }

    @Test
    public void testToString() {
        assertEquals(SOME_ROLE_NAME, new RoleGrantedAuthority(SOME_ROLE_NAME).toString());
    }

    @Test
    public void testEquals_Symmetric() {
        RoleGrantedAuthority x = new RoleGrantedAuthority(SOME_ROLE_NAME);
        RoleGrantedAuthority y = new RoleGrantedAuthority(SOME_ROLE_NAME);

        assertTrue(x.equals(x) && x.equals(x));
        assertTrue(x.hashCode() == x.hashCode());

        assertTrue(x.equals(y) && y.equals(x));
        assertTrue(x.hashCode() == y.hashCode());

        RoleGrantedAuthority z = new RoleGrantedAuthority(OTHER_ROLE_NAME);

        assertFalse(x.equals(z));
        assertFalse(z.equals(x));
        assertFalse(x.hashCode() == z.hashCode());

        Object otherObject = new Object();

        assertFalse(x.equals(otherObject));
        assertFalse(otherObject.equals(x));
        assertFalse(x.hashCode() == otherObject.hashCode());
    }
}