package net.n2oapp.security.admin.sso.keycloak;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки для модуля взаимодействия с keycloak
 */
//@Data
@ConfigurationProperties("keycloak")
public class SsoKeycloakProperties {

    private String redirectUrl = "http://localhost:8085/admin";
    private String serverUrl = "http://127.0.0.1:8080/auth";
    private String realm = "security-admin";
    private String clientId = "security-admin-sso";
    private String adminClientId = "admin-cli";
    private String username = "restadmin";
    private String password;
    private Boolean sendVerifyEmail = true;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAdminClientId() {
        return adminClientId;
    }

    public void setAdminClientId(String adminClientId) {
        this.adminClientId = adminClientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getSendVerifyEmail() {
        return sendVerifyEmail;
    }

    public void setSendVerifyEmail(Boolean sendVerifyEmail) {
        this.sendVerifyEmail = sendVerifyEmail;
    }
}
