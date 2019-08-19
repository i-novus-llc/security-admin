package net.n2oapp.security.admin.service;

import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.impl.service.ClientServiceImpl;
import net.n2oapp.security.admin.model.Oauth2Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class Oauth2ClientService implements ClientDetailsService, ClientRegistrationService {

    @Autowired
    private ClientServiceImpl clientService;


    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Client client;
        try {
            client = clientService.findById(clientId);
        } catch (NoSuchElementException e) {
            throw new NoSuchClientException("Oauth2Client with id: " +
                    clientId + " does not exists");
        }
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
        try {
            client = clientService.findById(clientId);
        } catch (NoSuchElementException e) {
            throw new NoSuchClientException("Oauth2Client with id: " +
                    clientId + " does not exists");
        }
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
        client.setClientId(apiModel.getClientId());
        client.setClientSecret(apiModel.getClientSecret());
        client.setAuthorizedGrantTypes(apiModel.getAuthorizedGrantTypes());
        client.setRegisteredRedirectUri(apiModel.getRegisteredRedirectUri());
        client.setAccessTokenValiditySeconds(apiModel.getAccessTokenValiditySeconds());

        return client;

    }

    private Client apiModel(ClientDetails oauth2Model) {
        Client apiModel = new Client();
        apiModel.setClientId(oauth2Model.getClientId());
        apiModel.setClientSecret(oauth2Model.getClientSecret());
        apiModel.setAuthorizedGrantTypes(oauth2Model.getAuthorizedGrantTypes());
        apiModel.setRegisteredRedirectUri(oauth2Model.getRegisteredRedirectUri());
        apiModel.setAccessTokenValiditySeconds(oauth2Model.getAccessTokenValiditySeconds());

        return apiModel;
    }
}
