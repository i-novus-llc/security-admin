package net.n2oapp.framework.security.auth.oauth2;

import org.springframework.beans.factory.annotation.Value;

/**
 * Настройки аутентификации OAuth2 OpenId Connect
 */
public class OpenIdProperties {
    @Value("${n2o.security.oauth2.auth-server-uri}")
    private String authServerUrl;
    @Value("${n2o.security.oauth2.access-token-uri}")
    private String accessTokenUrl;
    @Value("${n2o.security.oauth2.user-authorization-uri}")
    private String userAuthorizationUrl;
    @Value("${n2o.security.oauth2.user-info-uri}")
    private String userInfoUrl;
    @Value("${n2o.security.oauth2.logout-uri}")
    private String logoutUrl;
    @Value("${n2o.security.oauth2.client-id}")
    private String clientId;
    @Value("${n2o.security.oauth2.client-secret}")
    private String clientSecret;
    @Value("${n2o.security.oauth2.scopes}")
    private String[] scopes;

    private String loginEndpoint = "/login";
    private String logoutEndpoint = "/logout";

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
        return concatServer(accessTokenUrl);
    }

    public void setAccessTokenUrl(String accessTokenUrl) {
        this.accessTokenUrl = accessTokenUrl;
    }

    public String getUserInfoUrl() {
        return concatServer(userInfoUrl);
    }

    public void setUserInfoUrl(String userInfoUrl) {
        this.userInfoUrl = userInfoUrl;
    }

    public String getUserAuthorizationUrl() {
        return concatServer(userAuthorizationUrl);
    }

    public void setUserAuthorizationUrl(String userAuthorizationUrl) {
        this.userAuthorizationUrl = userAuthorizationUrl;
    }

    public String getLogoutUrl() {
        return concatServer(logoutUrl);
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public String getAuthServerUrl() {
        return authServerUrl;
    }

    public void setAuthServerUrl(String authServerUrl) {
        this.authServerUrl = authServerUrl;
    }

    public String getLoginEndpoint() {
        return loginEndpoint;
    }

    public void setLoginEndpoint(String loginEndpoint) {
        this.loginEndpoint = loginEndpoint;
    }

    public String getLogoutEndpoint() {
        return logoutEndpoint;
    }

    public void setLogoutEndpoint(String logoutEndpoint) {
        this.logoutEndpoint = logoutEndpoint;
    }

    private String concatServer(String url) {
        if (url.startsWith("http")) {
            return url;
        } else {
            if (!url.startsWith("/"))
                throw new IllegalArgumentException("Part of url " + url + " must be started with /");
            return getAuthServerUrl() + url;
        }
    }
}
