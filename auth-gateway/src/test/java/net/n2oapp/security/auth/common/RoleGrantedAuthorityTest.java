package net.n2oapp.security.auth.common;

import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.junit.Assert;
import org.junit.Test;

import static net.n2oapp.security.auth.common.TestConstants.OTHER_ROLE_NAME;
import static net.n2oapp.security.auth.common.TestConstants.SOME_ROLE_NAME;

public class RoleGrantedAuthorityTest {

    private static final String DEFAULT_ROLE_PREFIX = "ROLE_";

    @Test
    public void testGetAuthority() {
        RoleGrantedAuthority auth = new RoleGrantedAuthority(SOME_ROLE_NAME);
        Assert.assertEquals(auth.getAuthority(), DEFAULT_ROLE_PREFIX + SOME_ROLE_NAME);
    }

    @Test
    public void testToString() {
        RoleGrantedAuthority auth = new RoleGrantedAuthority(SOME_ROLE_NAME);
        Assert.assertEquals(auth.toString(), SOME_ROLE_NAME);
    }

    @Test
    public void testEquals_Symmetric() {
        RoleGrantedAuthority x = new RoleGrantedAuthority(SOME_ROLE_NAME);
        RoleGrantedAuthority y = new RoleGrantedAuthority(SOME_ROLE_NAME);

        Assert.assertTrue(x.equals(x) && x.equals(x));
        Assert.assertTrue(x.hashCode() == x.hashCode());

        Assert.assertTrue(x.equals(y) && y.equals(x));
        Assert.assertTrue(x.hashCode() == y.hashCode());

        RoleGrantedAuthority z = new RoleGrantedAuthority(OTHER_ROLE_NAME);

        Assert.assertFalse(x.equals(z));
        Assert.assertFalse(z.equals(x));
        Assert.assertFalse(x.hashCode() == z.hashCode());

        Object otherObject = new Object();

        Assert.assertFalse(x.equals(otherObject));
        Assert.assertFalse(otherObject.equals(x));
        Assert.assertFalse(x.hashCode() == otherObject.hashCode());
    }
}