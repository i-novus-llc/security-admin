package net.n2oapp.security.admin.impl.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@Table(name = "client", schema = "sec")
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


}
