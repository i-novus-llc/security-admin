package net.n2oapp.security.admin.auth.server;

import net.n2oapp.security.auth.common.User;
import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Докладывает роли и пермишены в токен доступа
 */
public class GatewayAccessTokenConverter extends DefaultAccessTokenConverter {

    public GatewayAccessTokenConverter(UserAuthenticationConverter userAuthenticationConverter, List<String> tokenInclude) {
        setUserTokenConverter(userAuthenticationConverter);
        rolesInclude = tokenInclude.contains("roles");
        permissionsInclude = tokenInclude.contains("permissions");
    }

    private Boolean rolesInclude;
    private Boolean permissionsInclude;

    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        if (authentication.isClientOnly()) {
            List<String> roles = new ArrayList<>();
            List<String> permissions = new ArrayList<>();
            if (rolesInclude)
                token.getAdditionalInformation().put("roles", roles);
            if (permissionsInclude)
                token.getAdditionalInformation().put("permissions", permissions);
            if (authentication.getAuthorities() != null) {
                for (GrantedAuthority authority : authentication.getAuthorities()) {
                    if (authority instanceof RoleGrantedAuthority)
                        roles.add(((RoleGrantedAuthority) authority).getRole());
                    else if (authority instanceof PermissionGrantedAuthority)
                        permissions.add(((PermissionGrantedAuthority) authority).getPermission());
                }
            }
        }
        if (authentication.getUserAuthentication() != null
                && authentication.getUserAuthentication().getPrincipal() instanceof User) {
            User principal = (User) authentication.getUserAuthentication().getPrincipal();
            if (principal.getRegion() != null)
                token.getAdditionalInformation().put("region", principal.getRegion());

            if (principal.getDepartment() != null)
                token.getAdditionalInformation().put("department", principal.getDepartment());

            if (principal.getOrganization() != null)
                token.getAdditionalInformation().put("organization", principal.getOrganization());

            if (principal.getUserLevel() != null)
                token.getAdditionalInformation().put("userLevel", principal.getUserLevel());
        }
        return super.convertAccessToken(token, authentication);
    }
}
