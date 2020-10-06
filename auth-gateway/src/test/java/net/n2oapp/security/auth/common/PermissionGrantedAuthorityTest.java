package net.n2oapp.security.auth.common;

import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import org.junit.Assert;
import org.junit.Test;

import static net.n2oapp.security.auth.common.TestConstants.OTHER_PERMISSION;
import static net.n2oapp.security.auth.common.TestConstants.SOME_PERMISSION;

public class PermissionGrantedAuthorityTest {

    private static final String DEFAULT_PERMISSION_PREFIX = "PERMISSION_";

    @Test
    public void testGetAuthority() {
        PermissionGrantedAuthority auth = new PermissionGrantedAuthority(SOME_PERMISSION);
        Assert.assertEquals(auth.getAuthority(), DEFAULT_PERMISSION_PREFIX + SOME_PERMISSION);
    }

    @Test
    public void testGetPermission() {
        PermissionGrantedAuthority auth = new PermissionGrantedAuthority(SOME_PERMISSION);
        Assert.assertEquals(auth.getPermission(), SOME_PERMISSION);
    }

    @Test
    public void testToString() {
        PermissionGrantedAuthority auth = new PermissionGrantedAuthority(SOME_PERMISSION);
        Assert.assertEquals(auth.toString(), SOME_PERMISSION);
    }

    @Test
    public void testEquals_Symmetric() {
        PermissionGrantedAuthority x = new PermissionGrantedAuthority(SOME_PERMISSION);
        PermissionGrantedAuthority y = new PermissionGrantedAuthority(SOME_PERMISSION);

        Assert.assertTrue(x.equals(x) && x.equals(x));
        Assert.assertTrue(x.hashCode() == x.hashCode());

        Assert.assertTrue(x.equals(y) && y.equals(x));
        Assert.assertTrue(x.hashCode() == y.hashCode());

        PermissionGrantedAuthority z = new PermissionGrantedAuthority(OTHER_PERMISSION);

        Assert.assertFalse(x.equals(z));
        Assert.assertFalse(z.equals(x));
        Assert.assertFalse(x.hashCode() == z.hashCode());

        Object otherObject = new Object();

        Assert.assertFalse(x.equals(otherObject));
        Assert.assertFalse(otherObject.equals(x));
        Assert.assertFalse(x.hashCode() == otherObject.hashCode());
    }
}