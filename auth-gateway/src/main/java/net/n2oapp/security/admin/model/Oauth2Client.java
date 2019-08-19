package net.n2oapp.security.admin.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Oauth2Client implements ClientDetails {

    private String clientId;
    private String clientSecret;
    private Set<String> authorizedGrantTypes;
    private Set<String> registeredRedirectUri;
    private Integer accessTokenValiditySeconds;


    @Override
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public Set<String> getResourceIds() {
        return null;
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
        return null;
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
        return null;
    }


    @Override
    public Map<String, Object> getAdditionalInformation() {
        return null;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return null;
    }


}
