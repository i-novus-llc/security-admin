package net.n2oapp.security.admin.service;

import net.n2oapp.security.admin.Repo.ClientRepo;
import net.n2oapp.security.admin.entity.ClientEntity;
import net.n2oapp.security.admin.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class JpaClientDetailsService implements ClientDetailsService, ClientRegistrationService {

    @Autowired
    private ClientRepo clientRepo;


    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Optional<ClientEntity> clientEntity = clientRepo.findById(clientId);
        if (clientEntity.isEmpty()) throw new NoSuchClientException("Client with id: " +
                clientId + " does not exists");
        return model(clientEntity.get());
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        if (clientDetails == null) throw new IllegalArgumentException("Client details must not be null");
        if (clientRepo.existsById(clientDetails.getClientId()))
            throw new ClientAlreadyExistsException("Client with id: " +
                    clientDetails.getClientId() + " already exists");
        clientRepo.save(entity(clientDetails));

    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        if (clientDetails == null) throw new IllegalArgumentException("Client details must not be null");
        if (!clientRepo.existsById(clientDetails.getClientId())) throw new NoSuchClientException("Client with id: " +
                clientDetails.getClientId() + " does not exists");
        clientRepo.save(entity(clientDetails));
    }

    @Override
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        ClientEntity entity;
        try {
            entity = clientRepo.findById(clientId).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new NoSuchClientException("Client with id: " +
                    clientId + " does not exists");
        }
        entity.setClientSecret(secret);
        clientRepo.save(entity);
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        if (!clientRepo.existsById(clientId)) throw new NoSuchClientException("Client with id: " +
                clientId + " does not exists");
        clientRepo.deleteById(clientId);
    }

    @Override
    public List<ClientDetails> listClientDetails() {
        List<ClientDetails> clientDetails = new ArrayList<>();
        clientRepo.findAll().forEach(e -> clientDetails.add(model(e)));
        return clientDetails;
    }

    private HashSet<String> stringToSet(String string) {
        return new HashSet<>(Arrays.asList(StringUtils.tokenizeToStringArray(string, ",")));
    }

    private Client model(ClientEntity clientEntity) {
        Client client = new Client();
        client.setClientId(clientEntity.getClientId());
        client.setClientSecret(clientEntity.getClientSecret());
        client.setAuthorizedGrantTypes(stringToSet(clientEntity.getAuthorizedGrantTypes()));
        client.setRegisteredRedirectUri(stringToSet(clientEntity.getRegisteredRedirectUri()));
        client.setAccessTokenValiditySeconds(clientEntity.getAccessTokenValiditySeconds());

        return client;

    }

    private ClientEntity entity(ClientDetails clientDetails) {
        Client client;
        if (clientDetails instanceof Client) {
            client = (Client) clientDetails;
        } else throw new IllegalArgumentException("net.n2oapp.security.admin.model.Client expected");
        ClientEntity entity = new ClientEntity();
        entity.setClientId(client.getClientId());
        entity.setClientSecret(client.getClientSecret());
        entity.setAuthorizedGrantTypes(StringUtils.collectionToCommaDelimitedString(client.getAuthorizedGrantTypes()));
        entity.setRegisteredRedirectUri(StringUtils.collectionToCommaDelimitedString(client.getRegisteredRedirectUri()));
        entity.setAccessTokenValiditySeconds(client.getAccessTokenValiditySeconds());

        return entity;
    }
}
