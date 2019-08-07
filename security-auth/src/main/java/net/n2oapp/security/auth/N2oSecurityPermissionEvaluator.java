package net.n2oapp.security.auth;

import net.n2oapp.security.user.authority.PermissionGrantedAuthority;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

/**
 * Реализация метода проверки наличия права доступа в тегах {@code <sec:authorize access="hasPermission(#this,'read')">}
 */
public class N2oSecurityPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object o, Object o1) {
        if (!(o1 instanceof String)) return false;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority instanceof PermissionGrantedAuthority) {
                if (authority.getAuthority().equals(o1)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}
