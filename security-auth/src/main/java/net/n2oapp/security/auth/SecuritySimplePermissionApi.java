package net.n2oapp.security.auth;

import net.n2oapp.framework.access.simple.PermissionApi;
import net.n2oapp.framework.api.user.UserContext;
import net.n2oapp.security.auth.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.authority.RoleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * Интерфейс для проверки прав и ролей пользователя с помощью Spring Security
 */
public class SecuritySimplePermissionApi implements PermissionApi {

    @Deprecated
    public boolean hasPermission(UserContext user, String permissionId) {
        return SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                        .filter(a -> a instanceof PermissionGrantedAuthority)
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(permissionId));
    }

    @Override
    public boolean hasRole(UserContext user, String roleId) {
        return SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                        .filter(a -> a instanceof RoleGrantedAuthority)
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleId));
    }
    @Override
    public boolean hasAuthentication(UserContext user) {
        return SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null;
    }

    @Override
    public boolean hasUsername(UserContext user, String name) {
        return SecurityContextHolder.getContext().getAuthentication() != null
                && Objects.equals(UserParamsUtil.getUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), name);
    }
}
