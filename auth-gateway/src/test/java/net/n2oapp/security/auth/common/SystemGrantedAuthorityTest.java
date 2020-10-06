package net.n2oapp.security.auth.common;

import net.n2oapp.security.auth.common.authority.SystemGrantedAuthority;
import org.junit.Assert;
import org.junit.Test;

import static net.n2oapp.security.auth.common.TestConstants.OTHER_SYSTEM_NAME;
import static net.n2oapp.security.auth.common.TestConstants.SOME_SYSTEM_NAME;

public class SystemGrantedAuthorityTest {

    private static final String DEFAULT_SYSTEM_PREFIX = "SYSTEM_";

    @Test
    public void testGetAuthority() {
        SystemGrantedAuthority auth = new SystemGrantedAuthority(SOME_SYSTEM_NAME);
        Assert.assertEquals(auth.getAuthority(), DEFAULT_SYSTEM_PREFIX + SOME_SYSTEM_NAME);
    }

    @Test
    public void testGetSystem() {
        SystemGrantedAuthority auth = new SystemGrantedAuthority(SOME_SYSTEM_NAME);
        Assert.assertEquals(auth.getSystem(), SOME_SYSTEM_NAME);
    }

    @Test
    public void testToString() {
        SystemGrantedAuthority auth = new SystemGrantedAuthority(SOME_SYSTEM_NAME);
        Assert.assertEquals(auth.toString(), SOME_SYSTEM_NAME);
    }

    @Test
    public void testEquals_Symmetric() {
        SystemGrantedAuthority x = new SystemGrantedAuthority(SOME_SYSTEM_NAME);
        SystemGrantedAuthority y = new SystemGrantedAuthority(SOME_SYSTEM_NAME);

        Assert.assertTrue(x.equals(x) && x.equals(x));
        Assert.assertTrue(x.hashCode() == x.hashCode());

        Assert.assertTrue(x.equals(y) && y.equals(x));
        Assert.assertTrue(x.hashCode() == y.hashCode());

        SystemGrantedAuthority z = new SystemGrantedAuthority(OTHER_SYSTEM_NAME);

        Assert.assertFalse(x.equals(z));
        Assert.assertFalse(z.equals(x));
        Assert.assertFalse(x.hashCode() == z.hashCode());

        Object otherObject = new Object();

        Assert.assertFalse(x.equals(otherObject));
        Assert.assertFalse(otherObject.equals(x));
        Assert.assertFalse(x.hashCode() == otherObject.hashCode());
    }
}