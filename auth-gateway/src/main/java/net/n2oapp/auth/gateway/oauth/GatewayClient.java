package net.n2oapp.auth.gateway.oauth;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.StringUtils;

import java.util.*;

public class GatewayClient implements ClientDetails {
    private String clientId;
    private String clientSecret;
    private Set<String> authorizedGrantTypes;
    private Set<String> registeredRedirectUri;
    private Integer accessTokenValiditySeconds;
    private Integer refreshTokenValiditySeconds;
    private List<Role> roles;


    @Override
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public Set<String> getResourceIds() {
        return Collections.emptySet();
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return true;
    }

    @Override
    public boolean isScoped() {
        return false;
    }

    @Override
    public boolean isSecretRequired() {
        return !StringUtils.isEmpty(clientSecret);
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    public Set<String> getScope() {
        return Set.of("read", "write");
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(Set<String> authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }

    public void setRegisteredRedirectUri(Set<String> registeredRedirectUri) {
        this.registeredRedirectUri = registeredRedirectUri;
    }


    @Override
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }


    @Override
    public Map<String, Object> getAdditionalInformation() {
        return Collections.emptyMap();
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> result = new ArrayList<>();
        Set<RoleGrantedAuthority> roles = new HashSet<>();
        Set<PermissionGrantedAuthority> permissions = new HashSet<>();
        if (this.roles != null) {
            for (Role role : this.roles) {
                roles.add(new RoleGrantedAuthority(role.getCode()));
                if (role.getPermissions() != null) {
                    for (Permission permission : role.getPermissions()) {
                        permissions.add(new PermissionGrantedAuthority(permission.getCode()));
                    }
                }
            }
            result.addAll(roles);
            result.addAll(permissions);
        }
        return result;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
