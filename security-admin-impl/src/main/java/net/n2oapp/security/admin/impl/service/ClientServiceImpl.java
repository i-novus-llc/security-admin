package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.impl.entity.ClientEntity;
import net.n2oapp.security.admin.impl.repository.ClientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

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
        Client client = this.findById(id);
        clientRepo.deleteById(client.getId());
    }

    @Override
    public Client findById(String id) {
        ClientEntity entity = clientRepo.findByClientId(id);
        if (entity == null) throw new NoSuchElementException();
        return model(entity);
    }

    @Override
    public List<Client> findAll() {
        List<Client> clientList = new ArrayList<>();
        clientRepo.findAll().forEach(clientEntity -> clientList.add(model(clientEntity)));
        return clientList;
    }

    @Override
    public boolean existsById(String id) {
        this.findById(id);
        return true;
    }

    private HashSet<String> stringToSet(String string) {
        return new HashSet<>(Arrays.asList(StringUtils.tokenizeToStringArray(string, ",")));
    }

    private Client model(ClientEntity clientEntity) {
        Client client = new Client();
        client.setId(clientEntity.getId());
        client.setClientId(clientEntity.getClientId());
        client.setClientSecret(clientEntity.getClientSecret());
        client.setAuthorizedGrantTypes(stringToSet(clientEntity.getAuthorizedGrantTypes()));
        client.setRegisteredRedirectUri(stringToSet(clientEntity.getRegisteredRedirectUri()));
        client.setAccessTokenValiditySeconds(clientEntity.getAccessTokenValiditySeconds());
        client.setRefreshTokenValiditySeconds(clientEntity.getRefreshTokenValiditySeconds());
        client.setLogoutUrl(clientEntity.getLogoutUrl());

        return client;

    }

    private ClientEntity entity(Client client) {
        ClientEntity entity = new ClientEntity();
        entity.setId(client.getId());
        entity.setClientId(client.getClientId());
        entity.setClientSecret(client.getClientSecret());
        entity.setAuthorizedGrantTypes(StringUtils.collectionToCommaDelimitedString(client.getAuthorizedGrantTypes()));
        entity.setRegisteredRedirectUri(StringUtils.collectionToCommaDelimitedString(client.getRegisteredRedirectUri()));
        entity.setAccessTokenValiditySeconds(client.getAccessTokenValiditySeconds());
        entity.setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds());
        entity.setLogoutUrl(client.getLogoutUrl());

        return entity;
    }
}
