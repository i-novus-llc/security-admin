package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.ClientCriteria;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.impl.entity.ClientEntity;
import net.n2oapp.security.admin.impl.repository.ClientRepository;
import net.n2oapp.security.admin.impl.service.specification.ClientSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;


    @Override
    public Client create(Client client) {
        if (clientRepository.findById(client.getClientId()).orElse(null) != null)
            throw new UserException("exception.uniqueClient");
        return model(clientRepository.save(entity(client)));
    }

    @Override
    public Client update(Client client) {
        clientNotExists(client.getClientId());
        return model(clientRepository.save(entity(client)));
    }

    @Override
    public void delete(String clientId) {
        clientNotExists(clientId);
        clientRepository.deleteById(clientId);
    }

    @Override
    public Client findById(String clientId) {
        return model(clientRepository.findById(clientId).orElse(null));
    }

    @Override
    public Page<Client> findAll(ClientCriteria criteria) {
        Specification<ClientEntity> specification = new ClientSpecifications(criteria);
        return clientRepository.findAll(specification, criteria).map(this::model);
    }

    private HashSet<String> stringToSet(String string) {
        return new HashSet<>(Arrays.asList(StringUtils.tokenizeToStringArray(string, ",")));
    }

    private Client model(ClientEntity clientEntity) {
        if (clientEntity == null) return null;
        Client client = new Client();
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
        entity.setClientId(client.getClientId());
        entity.setClientSecret(client.getClientSecret());
        entity.setAuthorizedGrantTypes(StringUtils.collectionToCommaDelimitedString(client.getAuthorizedGrantTypes()));
        entity.setRegisteredRedirectUri(StringUtils.collectionToCommaDelimitedString(client.getRegisteredRedirectUri()));
        entity.setAccessTokenValiditySeconds(client.getAccessTokenValiditySeconds());
        entity.setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds());
        entity.setLogoutUrl(client.getLogoutUrl());

        return entity;
    }

    private void clientNotExists(String id) {
        if (clientRepository.findById(id).orElse(null) == null)
            throw new UserException("exception.clientNotFound");
    }

}
