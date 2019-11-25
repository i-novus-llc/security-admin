package net.n2oapp.security.auth.common.authority;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

/**
 * Полномочие, основанное на доступе к определенной системе
 */
public class SystemGrantedAuthority implements GrantedAuthority {

    private static final String DEFAULT_SYSTEM_PREFIX = "SYSTEM_";

    private final String system;

    public SystemGrantedAuthority(String system) {
        Assert.hasText(system, "A granted authority textual representation is required");
        this.system = system;
    }

    @Override
    public String getAuthority() {
        return DEFAULT_SYSTEM_PREFIX + system;
    }

    public String getSystem() {
        return system;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof SystemGrantedAuthority) {
            return system.equals(((SystemGrantedAuthority) obj).system);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.system.hashCode();
    }

    @Override
    public String toString() {
        return system;
    }
}
