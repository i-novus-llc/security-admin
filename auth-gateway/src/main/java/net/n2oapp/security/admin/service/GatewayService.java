package net.n2oapp.security.admin.service;

import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.model.GatewayClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;

@Service
public class GatewayService implements ClientDetailsService {

    @Autowired
    private ClientService clientService;


    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Client client;
        client = clientService.findById(clientId);
        if (client == null)
            throw new NoSuchClientException("GatewayClient with id: " +
                    clientId + " does not exists");
        return gatewayClient(client);
    }


    private GatewayClient gatewayClient(Client apiModel) {
        GatewayClient client = new GatewayClient();
        client.setClientId(apiModel.getClientId());
        client.setClientSecret(apiModel.getClientSecret());
        client.setAuthorizedGrantTypes(apiModel.getGrantTypes());
        client.setRegisteredRedirectUri(apiModel.getRedirectUris());
        client.setAccessTokenValiditySeconds(apiModel.getAccessTokenLifetime());
        client.setRefreshTokenValiditySeconds(apiModel.getRefreshTokenLifetime());
        client.setLogoutUrl(apiModel.getLogoutUrl());

        return client;

    }

}
