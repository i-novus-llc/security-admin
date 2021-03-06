package net.n2oapp.security.admin.auth.server;

import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class GatewayService implements ClientDetailsService {

    @Autowired
    private ClientService clientService;

    @Value("${access.auth.access-token-lifetime:60}")
    private int accessTokenValidityMinutes;

    @Value("${access.auth.refresh-token-lifetime:1440}")
    private int refreshTokenValidityMinutes;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Client client;
        client = clientService.findByClientId(clientId);
        if (isNull(client))
            throw new NoSuchClientException("GatewayClient with id: " +
                    clientId + " does not exists");
        return gatewayClient(client);
    }

    private GatewayClient gatewayClient(Client apiModel) {
        GatewayClient client = new GatewayClient();
        client.setClientId(apiModel.getClientId());
        client.setClientSecret(apiModel.getClientSecret());

        Set<String> authorizedGrantTypes = new HashSet<>();
        if (Boolean.TRUE.equals(apiModel.getIsClientGrant())) authorizedGrantTypes.add("client_credentials");
        if (Boolean.TRUE.equals(apiModel.getIsAuthorizationCode())) {
            authorizedGrantTypes.add("authorization_code");
            authorizedGrantTypes.add("refresh_token");
        }
        if (Boolean.TRUE.equals(apiModel.getIsResourceOwnerPass())) authorizedGrantTypes.add("password");
        client.setAuthorizedGrantTypes(authorizedGrantTypes);
        String redirectUris = nonNull(apiModel.getRedirectUris()) ? apiModel.getRedirectUris().replace(" ", ",") : null;
        client.setRegisteredRedirectUri(StringUtils.commaDelimitedListToSet(redirectUris));
        if (nonNull(apiModel.getAccessTokenValidityMinutes())) {
            client.setAccessTokenValiditySeconds(apiModel.getAccessTokenValidityMinutes() * 60);
        } else client.setAccessTokenValiditySeconds(accessTokenValidityMinutes * 60);
        if (nonNull(apiModel.getRefreshTokenValidityMinutes())) {
            client.setRefreshTokenValiditySeconds(apiModel.getRefreshTokenValidityMinutes() * 60);
        } else client.setRefreshTokenValiditySeconds(refreshTokenValidityMinutes * 60);
        client.setRoles(apiModel.getRoles());
        return client;
    }
}
