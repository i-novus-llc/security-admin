package net.n2oapp.security.admin.service;

import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.model.Oauth2Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;

@Service
public class Oauth2ClientService implements ClientDetailsService {

    @Autowired
    private ClientService clientService;


    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Client client;
        client = clientService.findById(clientId);
        if (client == null)
            throw new NoSuchClientException("Oauth2Client with id: " +
                    clientId + " does not exists");
        return oauth2Model(client);
    }


    private Oauth2Client oauth2Model(Client apiModel) {
        Oauth2Client client = new Oauth2Client();
        client.setClientId(apiModel.getClientId());
        client.setClientSecret(apiModel.getClientSecret());
        client.setAuthorizedGrantTypes(apiModel.getAuthorizedGrantTypes());
        client.setRegisteredRedirectUri(apiModel.getRegisteredRedirectUri());
        client.setAccessTokenValiditySeconds(apiModel.getAccessTokenValiditySeconds());
        client.setRefreshTokenValiditySeconds(apiModel.getRefreshTokenValiditySeconds());
        client.setLogoutUrl(apiModel.getLogoutUrl());

        return client;

    }

}
