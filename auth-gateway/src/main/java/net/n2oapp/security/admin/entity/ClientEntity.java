package net.n2oapp.security.admin.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "oauth_client_details", schema = "sec")
public class ClientEntity {

    @Id
    @Column(name = "client_id", nullable = false)
    private String clientId;
    @Column(name = "client_secret")
    private String clientSecret;
    @Column(name = "authorized_grant_types")
    private String authorizedGrantTypes;

    @Column(name = "registered_redirect_uri")
    private String registeredRedirectUri;

    @Column(name = "access_token_validity_seconds")
    private Integer accessTokenValiditySeconds;

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


    public String getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public String getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }

    public void setRegisteredRedirectUri(String registeredRedirectUri) {
        this.registeredRedirectUri = registeredRedirectUri;
    }

    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientEntity)) return false;
        ClientEntity entity = (ClientEntity) o;
        return clientId.equals(entity.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId);
    }

}
