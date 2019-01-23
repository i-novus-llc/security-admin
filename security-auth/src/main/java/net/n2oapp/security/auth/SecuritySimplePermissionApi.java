package net.n2oapp.security.auth;

import net.n2oapp.framework.access.simple.PermissionApi;
import net.n2oapp.framework.api.user.UserContext;
import net.n2oapp.security.auth.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.authority.RoleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


/**
 * Интерфейс для проверки прав и ролей пользователя с помощью Spring Security
 */
public class SecuritySimplePermissionApi implements PermissionApi {

    @Deprecated
    public boolean hasPermission(UserContext user, String permissionId) {
        UserDetails userDetails = UserParamsUtil.getUserDetails();
        return userDetails != null && userDetails.getAuthorities().stream()
                        .filter(a -> a instanceof PermissionGrantedAuthority)
                        .anyMatch(grantedAuthority -> ((PermissionGrantedAuthority)grantedAuthority).getPermission().equalsIgnoreCase(permissionId));
    }

    @Override
    public boolean hasRole(UserContext user, String roleId) {
        UserDetails userDetails = UserParamsUtil.getUserDetails();
        return userDetails != null && userDetails.getAuthorities().stream()
                        .filter(a -> a instanceof RoleGrantedAuthority)
                        .anyMatch(grantedAuthority -> ((RoleGrantedAuthority)grantedAuthority).getRole().equalsIgnoreCase(roleId));
    }
    @Override
    public boolean hasAuthentication(UserContext user) {
        UserDetails userDetails = UserParamsUtil.getUserDetails();
        return userDetails != null && userDetails.isEnabled();
    }

    @Override
    public boolean hasUsername(UserContext user, String name) {
        return UserParamsUtil.getUsername().equalsIgnoreCase(name);
    }
}
