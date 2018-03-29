package net.n2oapp.framework.security.auth.oauth2;

import org.springframework.beans.factory.annotation.Value;

/**
 * Настройки аутентификации OAuth2 OpenId Connect
 */
public class OpenIdProperties {
    @Value("${n2o.security.oauth2.access-token-uri}")
    private String accessTokenUrl;
    @Value("${n2o.security.oauth2.user-authorization-uri}")
    private String userAuthorizationUrl;
    @Value("${n2o.security.oauth2.user-info-uri}")
    private String userInfoUrl;
    @Value("${n2o.security.oauth2.client-id}")
    private String clientId;
    @Value("${n2o.security.oauth2.client-secret}")
    private String clientSecret;
    @Value("${n2o.security.oauth2.scopes}")
    private String[] scopes;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String[] getScopes() {
        return scopes;
    }

    public void setScopes(String[] scopes) {
        this.scopes = scopes;
    }

    public String getAccessTokenUrl() {
        return accessTokenUrl;
    }

    public void setAccessTokenUrl(String accessTokenUrl) {
        this.accessTokenUrl = accessTokenUrl;
    }

    public String getUserInfoUrl() {
        return userInfoUrl;
    }

    public void setUserInfoUrl(String userInfoUrl) {
        this.userInfoUrl = userInfoUrl;
    }

    public String getUserAuthorizationUrl() {
        return userAuthorizationUrl;
    }

    public void setUserAuthorizationUrl(String userAuthorizationUrl) {
        this.userAuthorizationUrl = userAuthorizationUrl;
    }
}
