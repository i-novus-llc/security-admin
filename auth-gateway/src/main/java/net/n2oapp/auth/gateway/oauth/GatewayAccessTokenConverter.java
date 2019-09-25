package net.n2oapp.auth.gateway.oauth;

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

    public GatewayAccessTokenConverter(UserAuthenticationConverter userAuthenticationConverter) {
        setUserTokenConverter(userAuthenticationConverter);
    }

    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        if (authentication.isClientOnly()) {
            List<String> roles = new ArrayList<>();
            List<String> permissions = new ArrayList<>();
            token.getAdditionalInformation().put("roles", roles);
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
        return super.convertAccessToken(token, authentication);
    }
}
