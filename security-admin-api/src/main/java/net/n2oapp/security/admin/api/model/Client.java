package net.n2oapp.security.admin.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Клиент(Приложение)
 */

@Data
@NoArgsConstructor
public class Client {
    private String clientId;
    private String clientSecret;
    private Set<String> authorizedGrantTypes;
    private Set<String> registeredRedirectUri;
    private Integer accessTokenValiditySeconds;

}
