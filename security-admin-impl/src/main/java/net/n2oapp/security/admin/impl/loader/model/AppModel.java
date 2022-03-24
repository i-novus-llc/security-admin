package net.n2oapp.security.admin.impl.loader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppModel {
    @JsonProperty
    private String code;
    @JsonProperty
    private String name;
    @JsonProperty
    private String clientId;
    @JsonProperty
    private String clientSecret;
    @JsonProperty
    private String grantTypes;
    @JsonProperty
    private Integer accessTokenLifetime;
    @JsonProperty
    private Integer refreshTokenLifetime;
    @JsonProperty
    private String redirectUris;
    @JsonProperty
    private String logoutUrl;
}
