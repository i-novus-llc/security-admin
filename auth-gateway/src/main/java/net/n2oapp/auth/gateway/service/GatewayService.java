package net.n2oapp.auth.gateway.service;

import net.n2oapp.auth.gateway.model.GatewayClient;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
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
        client.setRoles(apiModel.getRoles());
        return client;

    }

}