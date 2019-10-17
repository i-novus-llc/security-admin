package net.n2oapp.security.admin.auth.server;

import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Service
public class GatewayService implements ClientDetailsService {

    @Autowired
    private ClientService clientService;


    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Client client;
        client = clientService.findByClientId(clientId);
        if (client == null)
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
        if (Boolean.TRUE.equals(apiModel.getIsAuthorizationCode())) authorizedGrantTypes.add("authorization_code");
        if (Boolean.TRUE.equals(apiModel.getIsResourceOwnerPass())) authorizedGrantTypes.add("password");
        client.setAuthorizedGrantTypes(authorizedGrantTypes);
        String redirectUris = apiModel.getRedirectUris() != null ? apiModel.getRedirectUris().replace(" ", ",") : null;
        client.setRegisteredRedirectUri(StringUtils.commaDelimitedListToSet(redirectUris));
        client.setAccessTokenValiditySeconds(apiModel.getAccessTokenLifetime() * 60);
        client.setRefreshTokenValiditySeconds(apiModel.getRefreshTokenLifetime() * 60);
        client.setRoles(apiModel.getRoles());
        return client;

    }

}
