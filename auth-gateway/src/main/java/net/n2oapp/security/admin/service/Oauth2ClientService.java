package net.n2oapp.security.admin.service;

import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.model.Oauth2Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Oauth2ClientService implements ClientDetailsService, ClientRegistrationService {

    @Autowired
    private ClientService clientService;


    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Client client;
        client = clientService.findById(clientId);
        if (client == null) throw new NoSuchClientException("Oauth2Client with id: " +
                clientId + " does not exists");
        return oauth2Model(client);
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        if (clientDetails == null) throw new IllegalArgumentException("Oauth2Client details must not be null");
        if (clientService.existsById(clientDetails.getClientId()))
            throw new ClientAlreadyExistsException("Oauth2Client with id: " +
                    clientDetails.getClientId() + " already exists");
        clientService.create(apiModel(clientDetails));

    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        if (clientDetails == null) throw new IllegalArgumentException("Oauth2Client details must not be null");
        if (!clientService.existsById(clientDetails.getClientId()))
            throw new NoSuchClientException("Oauth2Client with id: " +
                    clientDetails.getClientId() + " does not exists");
        clientService.update(apiModel(clientDetails));
    }

    @Override
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        Client client;
        client = clientService.findById(clientId);
        if (client == null) throw new NoSuchClientException("Oauth2Client with id: " +
                clientId + " does not exists");

        client.setClientSecret(secret);
        clientService.update(client);
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        if (!clientService.existsById(clientId)) throw new NoSuchClientException("Oauth2Client with id: " +
                clientId + " does not exists");
        clientService.delete(clientId);
    }

    @Override
    public List<ClientDetails> listClientDetails() {
        List<ClientDetails> clientDetails = new ArrayList<>();
        clientService.findAll().forEach(e -> clientDetails.add(oauth2Model(e)));
        return clientDetails;
    }

    private Oauth2Client oauth2Model(Client apiModel) {
        Oauth2Client client = new Oauth2Client();
        client.setId(apiModel.getId());
        client.setClientId(apiModel.getClientId());
        client.setClientSecret(apiModel.getClientSecret());
        client.setAuthorizedGrantTypes(apiModel.getAuthorizedGrantTypes());
        client.setRegisteredRedirectUri(apiModel.getRegisteredRedirectUri());
        client.setAccessTokenValiditySeconds(apiModel.getAccessTokenValiditySeconds());
        client.setRefreshTokenValiditySeconds(apiModel.getRefreshTokenValiditySeconds());
        client.setLogoutUrl(apiModel.getLogoutUrl());

        return client;

    }

    private Client apiModel(ClientDetails clientDetails) {
        Client apiModel = new Client();
        Oauth2Client oauth2Model;
        if (clientDetails instanceof Oauth2Client) {
            oauth2Model = (Oauth2Client) clientDetails;
        } else throw new IllegalArgumentException("net.n2oapp.security.admin.model.Oauth2Client expected");

        apiModel.setId(oauth2Model.getId());
        apiModel.setClientId(oauth2Model.getClientId());
        apiModel.setClientSecret(oauth2Model.getClientSecret());
        apiModel.setAuthorizedGrantTypes(oauth2Model.getAuthorizedGrantTypes());
        apiModel.setRegisteredRedirectUri(oauth2Model.getRegisteredRedirectUri());
        apiModel.setAccessTokenValiditySeconds(oauth2Model.getAccessTokenValiditySeconds());
        apiModel.setRefreshTokenValiditySeconds(oauth2Model.getRefreshTokenValiditySeconds());
        apiModel.setLogoutUrl(oauth2Model.getLogoutUrl());
        return apiModel;
    }
}
