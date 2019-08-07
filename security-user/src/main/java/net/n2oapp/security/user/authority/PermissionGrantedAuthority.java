package net.n2oapp.security.user.authority;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

/**
 * Полномочие, основанное на правах доступа
 */
public class PermissionGrantedAuthority implements GrantedAuthority {

    private final String permission;

    public PermissionGrantedAuthority(String permission) {
        Assert.hasText(permission, "A granted authority textual representation is required");
        this.permission = permission;
    }

    public String getAuthority() {
        return permission;
    }

    public String getPermission() {
        return permission;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof PermissionGrantedAuthority) {
            return permission.equals(((PermissionGrantedAuthority) obj).permission);
        }

        return false;
    }

    public int hashCode() {
        return this.permission.hashCode();
    }

    public String toString() {
        return this.permission;
    }
}
