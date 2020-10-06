package net.n2oapp.security.auth.common;

import net.n2oapp.security.auth.common.authority.SystemGrantedAuthority;
import org.junit.Test;

import static net.n2oapp.security.auth.common.TestConstants.OTHER_SYSTEM_NAME;
import static net.n2oapp.security.auth.common.TestConstants.SOME_SYSTEM_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class SystemGrantedAuthorityTest {

    private static final String DEFAULT_SYSTEM_PREFIX = "SYSTEM_";

    @Test
    public void testGetAuthority() {
        SystemGrantedAuthority auth = new SystemGrantedAuthority(SOME_SYSTEM_NAME);
        assertEquals( DEFAULT_SYSTEM_PREFIX + SOME_SYSTEM_NAME, auth.getAuthority());
    }

    @Test
    public void testGetSystem() {
        assertEquals(SOME_SYSTEM_NAME, new SystemGrantedAuthority(SOME_SYSTEM_NAME).getSystem());
    }

    @Test
    public void testToString() {
        assertEquals(SOME_SYSTEM_NAME, new SystemGrantedAuthority(SOME_SYSTEM_NAME).toString());
    }

    @Test
    public void testEquals_Symmetric() {
        SystemGrantedAuthority x = new SystemGrantedAuthority(SOME_SYSTEM_NAME);
        SystemGrantedAuthority y = new SystemGrantedAuthority(SOME_SYSTEM_NAME);

        assertTrue(x.equals(x) && x.equals(x));
        assertTrue(x.hashCode() == x.hashCode());

        assertTrue(x.equals(y) && y.equals(x));
        assertTrue(x.hashCode() == y.hashCode());

        SystemGrantedAuthority z = new SystemGrantedAuthority(OTHER_SYSTEM_NAME);

        assertFalse(x.equals(z));
        assertFalse(z.equals(x));
        assertFalse(x.hashCode() == z.hashCode());

        Object otherObject = new Object();

        assertFalse(x.equals(otherObject));
        assertFalse(otherObject.equals(x));
        assertFalse(x.hashCode() == otherObject.hashCode());
    }
}