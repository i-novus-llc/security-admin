package net.n2oapp.security.auth.common.authority;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemGrantedAuthority)) return false;
        SystemGrantedAuthority that = (SystemGrantedAuthority) o;
        return Objects.equals(system, that.system);
    }

    @Override
    public int hashCode() {
        return Objects.hash(DEFAULT_SYSTEM_PREFIX, system);
    }

    @Override
    public String toString() {
        return system;
    }
}
