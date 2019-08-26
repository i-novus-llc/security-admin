package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.impl.entity.ClientEntity;
import net.n2oapp.security.admin.impl.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private ClientRepository clientRepository;


    @Override
    public Client create(Client client) {
        return model(clientRepository.save(entity(client)));
    }

    @Override
    public Client update(Client client) {
        return model(clientRepository.save(entity(client)));
    }

    @Override
    public void delete(String id) {
        Client client = this.findById(id);
        clientRepository.deleteById(client.getId());
    }

    @Override
    public Client findById(String id) {
        ClientEntity entity = clientRepository.findByClientId(id);
        return model(entity);
    }

    @Override
    public Page<Client> findAll() {
        List<Client> clientList = new ArrayList<>();
        clientRepository.findAll().forEach(clientEntity -> clientList.add(model(clientEntity)));
        return new PageImpl<>(clientList);
    }

    @Override
    public boolean existsById(String id) {
        return this.findById(id) == null ? false : true;
    }

    private HashSet<String> stringToSet(String string) {
        return new HashSet<>(Arrays.asList(StringUtils.tokenizeToStringArray(string, ",")));
    }

    private Client model(ClientEntity clientEntity) {
        if (clientEntity == null) return null;
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
        if (client == null) return null;
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
