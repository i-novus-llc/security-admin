package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.impl.entity.ClientEntity;
import net.n2oapp.security.admin.impl.repository.ClientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepo clientRepo;


    @Override
    public Client create(Client client) {
        return model(clientRepo.save(entity(client)));
    }

    @Override
    public Client update(Client client) {
        return model(clientRepo.save(entity(client)));
    }

    @Override
    public void delete(String id) {
        clientRepo.deleteById(id);
    }

    @Override
    public Client findById(String id) {
        return model(clientRepo.findById(id).orElseThrow());
    }

    @Override
    public List<Client> findAll() {
        List<Client> clientList = new ArrayList<>();
        clientRepo.findAll().forEach(clientEntity -> clientList.add(model(clientEntity)));
        return clientList;
    }

    @Override
    public boolean existsById(String id) {
        return clientRepo.existsById(id);
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

    private ClientEntity entity(Client client) {
        ClientEntity entity = new ClientEntity();
        entity.setClientId(client.getClientId());
        entity.setClientSecret(client.getClientSecret());
        entity.setAuthorizedGrantTypes(StringUtils.collectionToCommaDelimitedString(client.getAuthorizedGrantTypes()));
        entity.setRegisteredRedirectUri(StringUtils.collectionToCommaDelimitedString(client.getRegisteredRedirectUri()));
        entity.setAccessTokenValiditySeconds(client.getAccessTokenValiditySeconds());

        return entity;
    }
}
